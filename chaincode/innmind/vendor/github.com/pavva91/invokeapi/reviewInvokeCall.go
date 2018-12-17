/*
Created by Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com
*/
package invokeapi

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"github.com/pavva91/arglib"
	// a "github.com/pavva91/trustreputationledger/assets"
	a "github.com/pavva91/assets"
)

var reviewInvokeCallLog = shim.NewLogger("reviewInvokeCall")

/*
For now we want that the Review assets can only be added on the ledger (NO MODIFY, NO DELETE)
*/
// ========================================================================================================================
// Create Reviewed Feature Review - wrapper of CreateFeatureRelationAgentAndReputation called from chiancode's Invoke
// ========================================================================================================================
func CreateReview(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0               1                   2                     3                   4                        5         6
	// "WriterAgentId", "StartupAgentId", "ExpertAgentId", "ReviewedFeatureId", "ReviewedFeatureTxId", "ReviewedFeatureTimestamp", "Value"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 7)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	reviewInvokeCallLog.Info("- start init Feature Review")

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	writerAgentId := args[0]
	startupAgentId := args[1]
	expertAgentId := args[2]
	executedFeatureId := args[3]
	executedFeatureTxId := args[4]
	timestamp := args[5]
	value := args[6]

	var writerAgent a.Agent

	// ==== Check if already existing startupAgent ====
	startupAgent, errA := a.GetAgentNotFoundError(stub, startupAgentId)
	if errA != nil {
		reviewInvokeCallLog.Info("Failed to find startupAgent by id " + startupAgentId)
		return shim.Error("Failed to find startupAgent by id: " + errA.Error())
	}
	// ==== Check if already existing expertAgent ====
	expertAgent, errA := a.GetAgentNotFoundError(stub, expertAgentId)
	if errA != nil {
		reviewInvokeCallLog.Info("Failed to find expertAgent by id " + expertAgentId)
		return shim.Error("Failed to find expertAgent by id: " + errA.Error())
	}
	// ==== Check if WriterAgent == StartupAgent || ExpertAgent ====
	switch true {
	case startupAgentId == writerAgentId:
		writerAgent = startupAgent
	case expertAgentId == writerAgentId:
		writerAgent = expertAgent
	default:
		return shim.Error("Wrong Writer Agent Id: " + writerAgentId)
	}

	// TODO: Da levare in teoria
	// ==== Check if already existing executedFeature ====
	executedFeature, errS := a.GetFeatureNotFoundError(stub, executedFeatureId)
	if errS != nil {
		reviewInvokeCallLog.Info("Failed to find executedFeature by id " + executedFeatureId)
		return shim.Error("Failed to find executedFeature by id " + errS.Error())
	}

	// ==== Check if serviceReview already exists ====
	// TODO: Definire come creare evaluationId, per ora è composto dai due ID (writerAgentId + startupAgentId + expertAgentId + ReviewedFeatureTxId)
	evaluationId := writerAgentId + startupAgentId + expertAgentId + executedFeatureTxId
	serviceReviewAsBytes, err := stub.GetState(evaluationId)
	if err != nil {
		return shim.Error("Failed to get executedFeature startupAgent relation: " + err.Error())
	} else if serviceReviewAsBytes != nil {
		reviewInvokeCallLog.Info("This executedFeature startupAgent relation already exists with relationId: " + evaluationId)
		return shim.Error("This executedFeature startupAgent relation already exists with relationId: " + evaluationId)
	}

	// ==== Actual creation of Feature Review  ====
	serviceReview, err := a.CreateReview(evaluationId, writerAgentId, startupAgentId, expertAgentId, executedFeatureId, executedFeatureTxId, timestamp, value, stub)
	if err != nil {
		return shim.Error("Failed to create executedFeature startupAgent relation of executedFeature " + executedFeature.Name + " with startupAgent " + startupAgent.Name)
	}

	// ==== Indexing of serviceReview by Feature Tx Id ====

	// index create
	serviceTxIndexKey, serviceIndexError := a.CreateFeatureTxIndex(serviceReview, stub)
	if serviceIndexError != nil {
		return shim.Error(serviceIndexError.Error())
	}
	//  Note - passing a 'nil' emptyValue will effectively delete the key from state, therefore we pass null character as emptyValue
	//  Save index entry to state. Only the key Name is needed, no need to store a duplicate copy of the FeatureAgentRelation.
	emptyValue := []byte{0x00}
	// index save
	putStateError := stub.PutState(serviceTxIndexKey, emptyValue)
	if putStateError != nil {
		return shim.Error("Error  saving Feature index: " + putStateError.Error())
	}

	// ==== Indexing of serviceReview by Agent ====

	// index create
	startupExpertIndexKey, agentIndexError := a.CreateStartupExpertTimestampIndex(serviceReview, stub)
	if agentIndexError != nil {
		return shim.Error(agentIndexError.Error())
	}
	// index save
	putStateStartupExpertIndexError := stub.PutState(startupExpertIndexKey, emptyValue)
	if putStateStartupExpertIndexError != nil {
		return shim.Error("Error  saving Agent index: " + putStateStartupExpertIndexError.Error())
	}

	// ==== Review saved and indexed. Set Event ====

	eventPayload := "Created Review: " + evaluationId + " Startup agent ID: " + startupAgentId + ", Expert agent ID: " + expertAgentId
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("ReviewCreatedEvent", payloadAsBytes)
	if eventError != nil {
		reviewInvokeCallLog.Info("Error in event Creation: " + eventError.Error())
	} else {
		reviewInvokeCallLog.Info("Event Create Review OK")
	}

	// ==== AgentFeatureRelation saved & indexed. Return success ====
	reviewInvokeCallLog.Info("Servizio: " + executedFeature.Name + " evaluated by: " + writerAgent.Name + " relative to the transaction: " + executedFeatureTxId)
	return shim.Success(nil)
}

// ============================================================================================================================
// Query FeatureRelationAgent - wrapper of GetFeature called from the chaincode invoke
// ============================================================================================================================
func QueryReview(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "evaluationId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	evaluationId := args[0]

	// ==== get the serviceReview ====
	serviceReview, err := a.GetReviewNotFoundError(stub, evaluationId)
	if err != nil {
		reviewInvokeCallLog.Info("Failed to find serviceReview by id " + evaluationId)
		return shim.Error(err.Error())
	} else {
		reviewInvokeCallLog.Info("Review ID: " + serviceReview.ReviewId + ", Writer Agent: " + serviceReview.WriterAgentId + ", Startup Agent: " + serviceReview.StartupAgentId + ", Expert Agent: " + serviceReview.ExpertAgentId + ", of the Feature: " + serviceReview.ReviewedFeatureId + ", with ReviewedFeatureTimestamp: " + serviceReview.ReviewedFeatureTimestamp + ", with Review: " + serviceReview.Value)
		// ==== Marshal the Get Feature Review query result ====
		evaluationAsJSON, err := json.Marshal(serviceReview)
		if err != nil {
			return shim.Error(err.Error())
		}
		return shim.Success(evaluationAsJSON)
	}
}

// ========================================================================================================================
// Query by Reviewed Feature Tx Id - wrapper of GetByReviewedFeatureTxId called from chiancode's Invoke
// ========================================================================================================================
func QueryByReviewedFeatureTx(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "executedFeatureTxId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	executedFeatureTxId := args[0]

	// ==== Run the byReviewedFeatureTx query ====
	byReviewedFeatureTxIdQuery, err := a.GetByReviewedFeatureTx(executedFeatureTxId, stub)
	if err != nil {
		reviewInvokeCallLog.Info("Failed to get service evaluation for this serviceTxId: " + executedFeatureTxId)
		return shim.Error(err.Error())
	}

	// ==== Print the byFeature query result ====
	err = a.PrintByReviewedFeatureTxIdResultsIterator(byReviewedFeatureTxIdQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

// ========================================================================================================================
// Query by Startup Expert Timestamp - wrapper of GetByStartupExpertTimestamp called from chiancode's Invoke
// ========================================================================================================================
func QueryByStartupExpert(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0                1                   2
	// "startupAgentId", "expertAgentId","Timestamp"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 3)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	startupAgentId := args[0]
	expertAgentId := args[1]
	timestamp := args[2]

	// ==== Run the byReviewedFeatureTx query ====
	byReviewedFeatureTxIdQuery, err := a.GetByStartupExpertTimestamp(startupAgentId, expertAgentId, timestamp, stub)
	if err != nil {
		reviewInvokeCallLog.Info("Failed to get service evaluation for this startup: " + startupAgentId + " and expert: " + expertAgentId)
		return shim.Error(err.Error())
	}

	// ==== Print the byFeature query result ====
	err = a.PrintByStartupExpertTimestampResultsIterator(byReviewedFeatureTxIdQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

// =====================================================================================================================
// GetReviewsByReviewedFeatureTxId - wrapper of GetByReviewedFeatureTxId called from chiancode's Invoke,
// for looking for serviceReviews of a certain executedFeatureTxId
// return: FeatureReviews As JSON
// =====================================================================================================================
func GetReviewsByReviewedFeatureTxId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "ReviewedFeatureTxId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	executedFeatureTxId := args[0]

	// ==== Run the byFeature query ====
	byFeatureQuery, err := a.GetByReviewedFeatureTx(executedFeatureTxId, stub)
	if err != nil {
		reviewInvokeCallLog.Info("The service Tx Id " + executedFeatureTxId + " is not mapped with any service evaluation.")
		return shim.Error(err.Error())
	}

	// ==== Get the Agents for the byFeatureTxId query result ====
	serviceReviews, err := a.GetReviewSliceFromFeatureTxIdRangeQuery(byFeatureQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== Marshal the byFeatureTxId query result ====
	serviceReviewsAsJSON, err := json.Marshal(serviceReviews)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(serviceReviewsAsJSON)
}

// =====================================================================================================================
// GetReviewsByStartupExpertTimestamp - wrapper of GetByStartupExpertTimestamp called from chiancode's Invoke,
// for looking for serviceReviews of a certain Startup-Expert couple
// return: FeatureReviews As JSON
// =====================================================================================================================
func GetReviewsByStartupExpertTimestamp(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0          1         2
	// "Startup", "Expert","Timestamp"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 3)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	startupAgentId := args[0]
	expertAgentId := args[1]
	timestamp := args[2]

	// ==== Run the ByStartupExpert query ====
	byReviewedFeatureTxIdQuery, err := a.GetByStartupExpertTimestamp(startupAgentId, expertAgentId, timestamp, stub)
	if err != nil {
		reviewInvokeCallLog.Info("Failed to get service evaluation for this startup: " + startupAgentId + " and expert: " + expertAgentId)
		return shim.Error(err.Error())
	}

	// ==== Get the FeatureReviews for the byStartupExpert query result ====
	serviceReviews, err := a.GetReviewSliceFromStartupExpertTimestampRangeQuery(byReviewedFeatureTxIdQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== Marshal the byFeatureTxId query result ====
	serviceReviewsAsJSON, err := json.Marshal(serviceReviews)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(serviceReviewsAsJSON)
}
