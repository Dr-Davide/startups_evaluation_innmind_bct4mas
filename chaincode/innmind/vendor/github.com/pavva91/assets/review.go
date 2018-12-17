/*
Created by Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com
*/
package assets

import (
	"encoding/json"
	"errors"
	"github.com/hyperledger/fabric/core/chaincode/shim"
)

var reviewLog = shim.NewLogger("review")

// =====================================================================================================================
// Define the Feature Review structure
// =====================================================================================================================
// - ReviewId
// - AgentId
// UNIVOCAL: WriterAgentId, StartupAgentId, ExpertAgentId, ReviewedFeatureTxId
type Review struct {
	// 	reviewId := writerAgentId + startupAgentId + expertAgentId + reviewedFeatureTxId
	ReviewId                 string `json:"ReviewId"`
	WriterAgentId            string `json:"WriterAgentId"` // WriterAgentId = StartupAgentId || ExpertAgentId
	StartupAgentId           string `json:"StartupAgentId"`
	ExpertAgentId            string `json:"ExpertAgentId"`
	ReviewedFeatureId        string `json:"ReviewedFeatureId"`
	ReviewedFeatureTxid      string `json:"ReviewedFeatureTxid"` // Relativo all'esecuzione del servizio (TODO: a cosa serve?)
	ReviewedFeatureTimestamp string `json:"ReviewedFeatureTimestamp"`
	Value                    string `json:"Value"`
}

// ============================================================
// Create Feature Review - create a new feature review
// ============================================================
func CreateReview(reviewId string, writerAgentId string, startupAgentId string, expertAgentId string, reviewedFeatureId string, reviewedFeatureTxId string, timestamp string, value string, stub shim.ChaincodeStubInterface) (*Review, error) {
	// ==== Create marble object and marshal to JSON ====
	featureReview := &Review{reviewId, writerAgentId, startupAgentId, expertAgentId, reviewedFeatureId, reviewedFeatureTxId, timestamp, value}
	featureReviewJSONAsBytes, _ := json.Marshal(featureReview)

	// === Save Feature Review to state ===
	if err := stub.PutState(reviewId, featureReviewJSONAsBytes); err != nil {
		reviewLog.Error(err)
		return nil, err
	}

	return featureReview, nil
}

// =====================================================================================================================
// Create Reviewed Feature Transaction(Tx) Index - to do query based on Reviewed Feature Tx Id
// =====================================================================================================================
func CreateFeatureTxIndex(review *Review, stub shim.ChaincodeStubInterface) (featureTxIndexKey string, err error) {
	indexName := "featureTx~review"
	featureTxIndexKey, err = stub.CreateCompositeKey(indexName, []string{review.ReviewedFeatureTxid, review.ReviewId})
	if err != nil {
		reviewLog.Error(err)
		return featureTxIndexKey, err
	}
	return featureTxIndexKey, nil
}

// ============================================================================================================================
// Create Startup Agent - Expert Agent - Timestamp - Review Id Index - to do query based on Startup-Expert-Timestamp Reviews
// ============================================================================================================================
func CreateStartupExpertTimestampIndex(review *Review, stub shim.ChaincodeStubInterface) (agentFeatureIndex string, err error) {
	indexName := "startup~expert~timestamp~review"
	agentFeatureIndex, err = stub.CreateCompositeKey(indexName, []string{review.StartupAgentId, review.ExpertAgentId, review.ReviewedFeatureTimestamp, review.ReviewId})
	if err != nil {
		reviewLog.Error(err)
		return agentFeatureIndex, err
	}
	return agentFeatureIndex, nil
}

func CheckingCreatingIndexingReview(writerAgentId string, startupAgentId string, expertAgentId string, reviewedFeatureId string, reviewedFeatureTxId string, timestamp string, value string, stub shim.ChaincodeStubInterface) (*Review, error) {
	// ==== Check if featureReview already exists ====
	// TODO: Definire come creare reviewId, per ora è composto dai due ID (writerAgentId + startupAgentId + expertAgentId + ReviewedFeatureTxId)
	reviewId := writerAgentId + startupAgentId + expertAgentId + reviewedFeatureTxId
	featureReviewAsBytes, err := stub.GetState(reviewId)
	if err != nil {
		newError := errors.New("Failed to get reviewedFeature startupAgent relation: " + err.Error())
		reviewLog.Error(newError)
		return nil, newError
	} else if featureReviewAsBytes != nil {
		newError := errors.New("This reviewedFeature startupAgent relation already exists with relationId: " + reviewId)
		reviewLog.Error(newError)
		return nil, newError
	}

	// ==== Actual creation of Feature Review  ====
	featureReview, err := CreateReview(reviewId, writerAgentId, startupAgentId, expertAgentId, reviewedFeatureId, reviewedFeatureTxId, timestamp, value, stub)
	if err != nil {
		newError := errors.New("Failed to create reviewedFeature startupAgent relation of reviewedFeature " + reviewedFeatureId + " with startupAgent " + reviewedFeatureId)
		reviewLog.Error(newError)
		return nil, newError
	}

	// ==== Indexing of featureReview by Feature Tx Id ====

	// index create
	featureTxIndexKey, featureIndexError := CreateFeatureTxIndex(featureReview, stub)
	if featureIndexError != nil {
		newError := errors.New(featureIndexError.Error())
		reviewLog.Error(newError)
		return nil, newError
	}
	//  Note - passing a 'nil' emptyValue will effectively delete the key from state, therefore we pass null character as emptyValue
	//  Save index entry to state. Only the key Name is needed, no need to store a duplicate copy of the FeatureAgentRelation.
	emptyValue := []byte{0x00}
	// index save
	putStateError := stub.PutState(featureTxIndexKey, emptyValue)
	if putStateError != nil {
		newError := errors.New("Error  saving Feature index: " + putStateError.Error())
		reviewLog.Error(newError)
		return nil, newError
	}

	// ==== Indexing of featureReview by Agent ====

	// index create
	startupExpertIndexKey, agentIndexError := CreateStartupExpertTimestampIndex(featureReview, stub)
	if agentIndexError != nil {
		newError := errors.New(agentIndexError.Error())
		reviewLog.Error(newError)
		return nil, newError
	}
	// index save
	putStateStartupExpertIndexError := stub.PutState(startupExpertIndexKey, emptyValue)
	if putStateStartupExpertIndexError != nil {
		newError := errors.New("Error  saving Agent index: " + putStateStartupExpertIndexError.Error())
		reviewLog.Error(newError)
		return nil, newError
	}

	return featureReview, nil
}

// =====================================================================================================================
// Get Feature Agent Relation - get the feature agent relation asset from ledger - return (nil,nil) if not found
// =====================================================================================================================
func GetReview(stub shim.ChaincodeStubInterface, reviewId string) (Review, error) {
	var featureRelationAgent Review
	featureRelationAgentAsBytes, err := stub.GetState(reviewId) // getState retreives a key/value from the ledger
	if err != nil {                                             // this seems to always succeed, even if key didn't exist
		newError := errors.New("Error in finding feature relation with agent: " + err.Error())
		reviewLog.Error(newError)
		return featureRelationAgent, newError
	}

	json.Unmarshal(featureRelationAgentAsBytes, &featureRelationAgent) // un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo Review?)

	return featureRelationAgent, nil
}

// =====================================================================================================================
// Get Feature Agent Relation Not Found Error - get the feature agent relation asset from ledger - throws error if not found (error!=nil ---> key not found)
// =====================================================================================================================
func GetReviewNotFoundError(stub shim.ChaincodeStubInterface, reviewId string) (Review, error) {
	var featureRelationAgent Review
	featureRelationAgentAsBytes, err := stub.GetState(reviewId) // getState retreives a key/value from the ledger
	if err != nil {                                             // this seems to always succeed, even if key didn't exist
		newError := errors.New("Error in finding feature review: " + err.Error())
		reviewLog.Error(newError)
		return featureRelationAgent, newError
	}

	if featureRelationAgentAsBytes == nil {
		newError := errors.New("Feature Review non found, ReviewId: " + reviewId)
		reviewLog.Error(newError)
		return Review{}, newError
	}
	json.Unmarshal(featureRelationAgentAsBytes, &featureRelationAgent) // un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo Review)

	return featureRelationAgent, nil
}

// =====================================================================================================================
// Get the feature query on FeatureRelationAgent - Reviewe the query based on feature composite index
// =====================================================================================================================
func GetByReviewedFeatureTx(reviewedFeatureTxId string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the feature~agent~relation index by feature
	// This will reviewe a key range query on all keys starting with 'feature'
	indexName := "featureTx~review"
	reviewedFeatureTxResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{reviewedFeatureTxId})
	if err != nil {
		reviewLog.Error(err)
		return reviewedFeatureTxResultsIterator, err
	}
	return reviewedFeatureTxResultsIterator, nil
}

// =====================================================================================================================
// Get the agent query on FeatureRelationAgent - Reviewe the query based on agent composite index
// =====================================================================================================================
func GetByStartupExpertTimestamp(startupAgentId string, expertAgentId string, timestamp string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the feature~agent~relation index by feature
	// This will reviewe a key range query on all keys starting with 'feature'
	indexName := "startup~expert~timestamp~review"
	startupExpertResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{startupAgentId, expertAgentId, timestamp})
	if err != nil {
		reviewLog.Error(err)
		return startupExpertResultsIterator, err
	}
	return startupExpertResultsIterator, nil
}

// =====================================================================================================================
// Delete Feature Review - "removing"" a key/value from the ledger
// =====================================================================================================================
func DeleteFeatureReview(stub shim.ChaincodeStubInterface, reviewId string) error {
	// remove the featureRelationAgent
	err := stub.DelState(reviewId) // remove the key from chaincode state
	if err != nil {
		reviewLog.Error(err)
		return err
	}
	return nil
}

// =====================================================================================================================
// Delete Reviewed Feature Tx Index - "removing"" a key/value from the ledger
// =====================================================================================================================
func DeleteReviewedFeatureTxIndex(stub shim.ChaincodeStubInterface, reviewedFeatureTxId string, reviewId string) error {
	// remove the featureRelationAgent
	indexName := "featureTx~review"

	agentFeatureIndex, err := stub.CreateCompositeKey(indexName, []string{reviewedFeatureTxId, reviewId})
	if err != nil {
		reviewLog.Error(err)
		return err
	}
	err = stub.DelState(agentFeatureIndex) // remove the key from chaincode state
	if err != nil {
		reviewLog.Error(err)
		reviewLog.Error(err)
		return err
	}
	reviewLog.Info("DeleteReviewedFeatureTxIndex: DELETED - indexName: " + indexName + " , reviewedFeatureTxId: " + reviewedFeatureTxId + ", reviewId: " + reviewId)
	return nil
}

// =====================================================================================================================
// Delete Agent Feature Relation - delete from state and from marble index Shows Off DelState() - "removing"" a key/value from the ledger
// =====================================================================================================================
func DeleteStartupExpertIndex(stub shim.ChaincodeStubInterface, startupAgentId string, expertAgentId string, reviewId string) error {

	// indexName
	indexName := "startup~expert~review"

	// create the composite key
	agentFeatureIndex, err := stub.CreateCompositeKey(indexName, []string{startupAgentId, expertAgentId, reviewId})
	if err != nil {
		reviewLog.Error(err.Error())
		return err
	}

	// eliminate the record related to the composite key
	err = stub.DelState(agentFeatureIndex) // remove the key from chaincode state
	if err != nil {
		reviewLog.Error(err.Error())
		return err
	}
	reviewLog.Info("DeleteStartupExpertIndex: DELETED - indexName: " + indexName + " , startupAgentId: " + startupAgentId + ", expertAgentId: " + expertAgentId + ", reviewId: " + reviewId)
	return nil
}

// =====================================================================================================================
// GetFeatureRelationSliceFromFeatureTxRangeQuery - Get the Review Slices from the result of query "GetByReviewedFeatureTx"
// =====================================================================================================================
func GetReviewSliceFromFeatureTxIdRangeQuery(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) ([]Review, error) {
	var featureReviews []Review
	defer queryIterator.Close()

	for i := 0; queryIterator.HasNext(); i++ {
		responseRange, err := queryIterator.Next()
		if err != nil {
			reviewLog.Error(err.Error())
			return nil, err
		}
		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		reviewId := compositeKeyParts[1]

		ifeatureRelationAgent, err := GetReview(stub, reviewId)
		featureReviews = append(featureReviews, ifeatureRelationAgent)
		if err != nil {
			reviewLog.Error(err.Error())
			return nil, err
		}
		reviewLog.Info("- found a relation EVALUATION ID: %s \n", reviewId)
	}
	return featureReviews, nil
}

// =====================================================================================================================
// GetReviewSliceFromStartupExpertTimestampRangeQuery - Get the Agent and Review Slices from the result of query "GetByStartupExpertTimestamp"
// =====================================================================================================================
func GetReviewSliceFromStartupExpertTimestampRangeQuery(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) ([]Review, error) {
	var featureReviews []Review
	// USE DEFER BECAUSE it will close also in case of error throwing (premature return)
	defer queryIterator.Close()

	for i := 0; queryIterator.HasNext(); i++ {
		responseRange, err := queryIterator.Next()
		if err != nil {
			reviewLog.Error(err.Error())
			return nil, err
		}
		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		reviewId := compositeKeyParts[3]

		ifeatureRelationAgent, err := GetReview(stub, reviewId)
		featureReviews = append(featureReviews, ifeatureRelationAgent)
		if err != nil {
			reviewLog.Error(err.Error())
			return nil, err
		}
		reviewLog.Info("- found a relation EVALUATION ID: %s , VALUE: %s\n", ifeatureRelationAgent.ReviewId, ifeatureRelationAgent.Value)
	}
	return featureReviews, nil
}

// =====================================================================================================================
// Print Feature Tx Results Iterator - Print on screen the iterator of the reviewed feature tx id query result
// =====================================================================================================================
func PrintByReviewedFeatureTxIdResultsIterator(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) error {
	// USE DEFER BECAUSE it will close also in case of error throwing (premature return)
	defer queryIterator.Close()
	for i := 0; queryIterator.HasNext(); i++ {
		responseRange, err := queryIterator.Next()
		if err != nil {
			reviewLog.Error(err.Error())
			return err
		}
		// get the feature agent relation from feature~agent~relation composite key
		indexName, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		reviewedFeatureTxId := compositeKeyParts[0]
		reviewId := compositeKeyParts[1]

		if err != nil {
			reviewLog.Error(err.Error())
			return err
		}
		reviewLog.Info("- found a relation from OBJECT_TYPE:%s EXECUTED SERVICE TX ID:%s EVALUATION ID: %s\n", indexName, reviewedFeatureTxId, reviewId)
	}
	return nil
}

// =====================================================================================================================
// Print Startup Expert Results Iterator - Print on screen the general iterator of the startup expert index query result
// =====================================================================================================================
func PrintByStartupExpertTimestampResultsIterator(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) error {
	defer queryIterator.Close()
	for i := 0; queryIterator.HasNext(); i++ {
		responseRange, err := queryIterator.Next()
		if err != nil {
			reviewLog.Error(err.Error())
			return err
		}
		indexName, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		startupAgentId := compositeKeyParts[0]
		expertAgentId := compositeKeyParts[1]
		reviewId := compositeKeyParts[3]

		if err != nil {
			reviewLog.Error(err.Error())
			return err
		}
		reviewLog.Info("- found a relation from OBJECT_TYPE:%s Startup AGENT ID:%s Expert AGENT ID:%s  EVALUATION ID: %s\n", indexName, startupAgentId, expertAgentId, reviewId)
	}
	return nil
}
