/*
Package assets represent the assets with relatives base functions (create, indexing, queries) that can be stored in the ledger of hyperledger fabric blockchain
*/
/*
Created by Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com
*/
package assets

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"github.com/pavva91/arglib"
	"strconv"
)

var featureLog = shim.NewLogger("feature")

// =====================================================================================================================
// Define the Feature structure, with 3 properties.
// trying(https://medium.com/@wishmithasmendis/from-rdbms-to-key-value-store-data-modeling-techniques-a2874906bc46)
// =====================================================================================================================
// - FeatureId
// - Name
type Feature struct {
	FeatureId          string   `json:"FeatureId"`
	Name               string   `json:"Name"`
	FeatureComposition []string `json:"FeatureComposition"`
	// Description string `json:"Description"`
	// TODO: Spostare Description in FeatureRelationAgent
}

// We have 2 kind of Feature:
// - LeafFeature ----> with FeatureComposition = [] (zero-value)
// - CompositeFeature ----> with FeatureComposition = [LeafServi
// ce1, ... , LeafFeatureN]

// =====================================================================================================================
// CreateLeafFeature - create a new Leaf feature and return the created agent
// =====================================================================================================================
func CreateLeafFeature(featureId string, featureName string, stub shim.ChaincodeStubInterface) (*Feature, error) {
	// ==== Create marble object and marshal to JSON ====
	feature := &Feature{FeatureId: featureId, Name: featureName}
	feature2JSONAsBytes, err := json.Marshal(feature)
	if err != nil {
		return feature, errors.New("Failed Marshal feature: " + feature.Name)
	}

	// === Save marble to state ===
	stub.PutState(featureId, feature2JSONAsBytes)
	return feature, nil
}

// =====================================================================================================================
// CreateCompositeFeature - create a new Composite feature and return the created agent as a composition of (TODO:Leaf) Features
// =====================================================================================================================
func CreateCompositeFeature(featureId string, featureName string, featureComposition []string, stub shim.ChaincodeStubInterface) (*Feature, error) {
	if featureComposition == nil {
		return nil, errors.New("inserted null featureComposition, for composite feature has to be != nil")
	}
	// ==== Create marble object and marshal to JSON ====
	feature := &Feature{featureId, featureName, featureComposition}
	feature2JSONAsBytes, err := json.Marshal(feature)
	if err != nil {
		return feature, errors.New("Failed Marshal feature: " + feature.Name)
	}

	// === Save marble to state ===
	stub.PutState(featureId, feature2JSONAsBytes)
	return feature, nil
}

// =====================================================================================================================
// CreateFeature - create a new feature and return the created agent as a composition of Features
// =====================================================================================================================
func CreateFeature(featureId string, featureName string, featureComposition []string, stub shim.ChaincodeStubInterface) (*Feature, error) {
	// ==== Create marble object and marshal to JSON ====
	feature := &Feature{featureId, featureName, featureComposition}
	feature2JSONAsBytes, err := json.Marshal(feature)
	if err != nil {
		return feature, errors.New("Failed Marshal feature: " + feature.Name)
	}

	// === Save marble to state ===
	stub.PutState(featureId, feature2JSONAsBytes)
	transientMap, err := stub.GetTransient()
	transientData, ok := transientMap["event"]
	featureLog.Info("OK: " + strconv.FormatBool(ok))
	featureLog.Info(transientMap)
	featureLog.Info(transientData)

	return feature, nil
}

// =====================================================================================================================
// Create Feature's Name based Index - to do query based on Name of the Feature
// =====================================================================================================================
func CreateNameIndexFeature(featureToIndex *Feature, stub shim.ChaincodeStubInterface) (nameFeatureIndexKey string, err error) {
	//  ==== Index the featureAgentRelation to enable feature-based range queries, e.g. return all x features ====
	//  An 'index' is a normal key/value entry in state.
	//  The key is a composite key, with the elements that you want to range query on listed first.
	//  In our case, the composite key is based on feature~agent~relation.
	//  This will enable very efficient state range queries based on composite keys matching feature~agent~relation
	indexName := "name~featureId"
	nameFeatureIndexKey, err = stub.CreateCompositeKey(indexName, []string{featureToIndex.Name, featureToIndex.FeatureId})
	if err != nil {
		return nameFeatureIndexKey, err
	}
	return nameFeatureIndexKey, nil
}

// =====================================================================================================================
// Create Leaf Feature and create and save the index - Atomic function of 3 the subfunctions: save, index, saveindex
// =====================================================================================================================
func CreateAndIndexLeafFeature(featureId string, featureName string, stub shim.ChaincodeStubInterface) error {
	feature, err := CreateLeafFeature(featureId, featureName, stub)
	if err != nil {
		return errors.New("Failed to create the feature: " + err.Error())
	}

	// ==== Indexing of feature by Name (to do query by Name, if you want) ====
	// index create
	nameIndexKey, nameIndexError := CreateNameIndexFeature(feature, stub)
	if nameIndexError != nil {
		return errors.New(nameIndexError.Error())
	}
	featureLog.Info(nameIndexKey)
	// TODO: Mettere a Posto (fare un create Feature index

	saveIndexError := SaveIndex(nameIndexKey, stub)
	if saveIndexError != nil {
		return errors.New(saveIndexError.Error())
	}
	return nil

}

// =====================================================================================================================
// Create Composite Feature and create and save the index - Atomic function of 3 the subfunctions: save, index, saveindex
// =====================================================================================================================
func CreateAndIndexCompositeFeature(featureId string, featureName string, featureComposition []string, stub shim.ChaincodeStubInterface) error {
	feature, err := CreateCompositeFeature(featureId, featureName, featureComposition, stub)
	if err != nil {
		return errors.New("Failed to create the feature: " + err.Error())
	}

	// ==== Indexing of feature by Name (to do query by Name, if you want) ====
	// index create
	nameIndexKey, nameIndexError := CreateNameIndexFeature(feature, stub)
	if nameIndexError != nil {
		return errors.New(nameIndexError.Error())
	}
	featureLog.Info(nameIndexKey)
	// TODO: Mettere a Posto (fare un create Feature index

	saveIndexError := SaveIndex(nameIndexKey, stub)
	if saveIndexError != nil {
		return errors.New(saveIndexError.Error())
	}
	return nil

}

// =====================================================================================================================
// Create Feature and create and save the index - Atomic function of 3 the subfunctions: save, index, saveindex
// =====================================================================================================================
func CreateAndIndexFeature(featureId string, featureName string, featureComposition []string, stub shim.ChaincodeStubInterface) error {
	feature, err := CreateFeature(featureId, featureName, featureComposition, stub)
	if err != nil {
		return errors.New("Failed to create the feature: " + err.Error())
	}

	// ==== Indexing of feature by Name (to do query by Name, if you want) ====
	// index create
	nameIndexKey, nameIndexError := CreateNameIndexFeature(feature, stub)
	if nameIndexError != nil {
		return errors.New(nameIndexError.Error())
	}
	featureLog.Info(nameIndexKey)
	// TODO: Mettere a Posto (fare un create Feature index

	saveIndexError := SaveIndex(nameIndexKey, stub)
	if saveIndexError != nil {
		return errors.New(saveIndexError.Error())
	}
	return nil

}

// =====================================================================================================================
// ModifyFeatureName - Modify the feature name of the asset passed as parameter
// TODO: Give the permission of changing the feature name?
// =====================================================================================================================
func ModifyFeatureName(feature Feature, newFeatureName string, stub shim.ChaincodeStubInterface) error {

	feature.Name = newFeatureName

	featureAsBytes, _ := json.Marshal(feature)
	putStateError := stub.PutState(feature.FeatureId, featureAsBytes)
	if putStateError != nil {
		return errors.New(putStateError.Error())
	}
	return nil
}

// =====================================================================================================================
// Get Feature Not Found Error - get the feature asset from ledger -
// throws error if not found (error!=nil ---> key not found)
// =====================================================================================================================
func GetFeatureNotFoundError(stub shim.ChaincodeStubInterface, featureId string) (Feature, error) {
	var feature Feature
	featureAsBytes, err := stub.GetState(featureId) //getState retreives a key/value from the ledger
	if err != nil {                                 //this seems to always succeed, even if key didn't exist
		return feature, errors.New("Error in finding feature: " + err.Error())
	}
	featureLog.Info(featureAsBytes)
	featureLog.Info(feature)

	if featureAsBytes == nil {
		return feature, errors.New("Feature non found, FeatureId: " + featureId)
	}

	json.Unmarshal(featureAsBytes, &feature) //un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo Feature)

	featureLog.Info(feature)
	return feature, nil
}

// =====================================================================================================================
// Get Feature - get the feature asset from ledger - return (nil,nil) if not found
// =====================================================================================================================

func GetFeature(stub shim.ChaincodeStubInterface, featureId string) (Feature, error) {
	var feature Feature
	featureAsBytes, err := stub.GetState(featureId) //getState retreives a key/value from the ledger
	if err != nil {                                 //this seems to always succeed, even if key didn't exist
		return feature, errors.New("Error in finding feature: " + err.Error())
	}
	featureLog.Info(featureAsBytes)
	featureLog.Info(feature)

	json.Unmarshal(featureAsBytes, &feature) //un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo Feature)

	featureLog.Info(feature)
	return feature, nil
}

// =====================================================================================================================
// Get Feature as Bytes - get the feature as bytes from ledger
// =====================================================================================================================
func GetFeatureAsBytes(stub shim.ChaincodeStubInterface, idFeature string) ([]byte, error) {
	featureAsBytes, err := stub.GetState(idFeature) //getState retreives a key/value from the ledger
	if err != nil {                                 //this seems to always succeed, even if key didn't exist
		return featureAsBytes, errors.New("Failed to get feature - " + idFeature)
	}
	return featureAsBytes, nil
}

// =====================================================================================================================
// Get the name query on Feature - Execute the query based on feature name composite index
// =====================================================================================================================
func GetByFeatureName(featureName string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the feature~agent~relation index by feature
	// This will execute a key range query on all keys starting with 'feature'
	byFeatureNameResultIterator, err := stub.GetStateByPartialCompositeKey("name~featureId", []string{featureName})
	if err != nil {
		return byFeatureNameResultIterator, err
	}
	// defer byFeatureNameResultIterator.Close()
	return byFeatureNameResultIterator, nil
}

// =====================================================================================================================
// DeleteFeature() - remove a feature from state and from feature index
//
// Shows Off DelState() - "removing"" a key/value from the ledger
//
// Inputs:
//      0
//     FeatureId
// =====================================================================================================================
func DeleteFeature(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	featureLog.Info("Starting Delete Feature")

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	// input sanitation
	err := arglib.SanitizeArguments(args)
	if err != nil {
		return shim.Error(err.Error())
	}

	featureId := args[0]

	// get the feature
	feature, err := GetFeatureNotFoundError(stub, featureId)
	if err != nil {
		featureLog.Info("Failed to find feature by FeatureId " + featureId)
		return shim.Error(err.Error())
	}

	// TODO: Delete anche (prima) le relazioni del servizio con gli agenti
	err = DeleteAllFeatureAgentRelations(featureId, stub)
	if err != nil {
		return shim.Error("Failed to delete feature agent relation: " + err.Error())
	}

	// remove the feature
	err = stub.DelState(featureId) //remove the key from chaincode state
	if err != nil {
		return shim.Error("Failed to delete feature: " + err.Error())
	}

	featureLog.Info("Deleted feature: " + feature.Name)
	return shim.Success(nil)
}

// =====================================================================================================================
// DeleteAllFeatureAgentRelations - delete all the Feature relations with agent (aka: Reference Integrity)
// =====================================================================================================================
func DeleteAllFeatureAgentRelations(featureId string, stub shim.ChaincodeStubInterface) error {
	featureAgentResultsIterator, err := GetFeatureRelationAgentByFeature(featureId, stub)
	if err != nil {
		return err
	}
	for i := 0; featureAgentResultsIterator.HasNext(); i++ {
		responseRange, err := featureAgentResultsIterator.Next()
		if err != nil {
			return err
		}
		// get the feature agent relation from feature~agent~relation composite key
		objectType, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		featureId := compositeKeyParts[0]
		agentId := compositeKeyParts[1]
		relationId := compositeKeyParts[2]

		if err != nil {
			return err
		}

		fmt.Printf("Delete the relation: from composite key OBJECT_TYPE:%s SERVICE ID:%s AGENT ID:%s RELATION ID: %s\n", objectType, featureId, agentId, relationId)

		// remove the featureRelationAgent
		err = DeleteFeatureRelationAgent(stub, relationId) //remove the key from chaincode state
		if err != nil {
			return err
		}

		// remove the feature index
		err = DeleteFeatureIndexFeatureRelationAgent(stub, objectType, featureId, agentId, relationId) //remove the key from chaincode state
		if err != nil {
			return err
		}

	}
	return nil
}

// =====================================================================================================================
// GetFeatureSliceFromRangeQuery - Get the Feature Slices from the result of query "byFeatureName"
// =====================================================================================================================
func GetFeatureSliceFromRangeQuery(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) ([]Feature, error) {
	var featureSlice []Feature

	for i := 0; queryIterator.HasNext(); i++ {
		responseRange, err := queryIterator.Next()
		if err != nil {
			return nil, err
		}
		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		featureId := compositeKeyParts[1]

		feature, err := GetFeatureNotFoundError(stub, featureId)
		featureSlice = append(featureSlice, feature)
		if err != nil {
			return nil, err
		}
		fmt.Printf("- found a feature SERVICE ID: %s \n", featureId)
	}
	queryIterator.Close()
	return featureSlice, nil
}
