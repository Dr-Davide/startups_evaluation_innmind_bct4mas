/*
Created by Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com
*/
package assets

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
)

var featureRelationAgentLog = shim.NewLogger("featureRelationAgent")

type FeatureRelationAgent struct {
	RelationId  string `json:"RelationId"` // relationId := featureId + agentId
	FeatureId   string `json:"FeatureId"`
	AgentId     string `json:"AgentId"`
	Cost        string `json:"Cost"` //TODO: Usare float64
	Time        string `json:"Time"` //TODO: Usare float64
	Description string `json:"Description"`
}

// =====================================================================================================================
// createFeatureAgentMapping - create a new mapping feature agent
// =====================================================================================================================
func CreateFeatureRelationAgent(relationId string, featureId string, agentId string, cost string, time string, description string, stub shim.ChaincodeStubInterface) (*FeatureRelationAgent, error) {
	// ==== Create marble object and marshal to JSON ====
	featureRelationAgent := &FeatureRelationAgent{relationId, featureId, agentId, cost, time, description}
	featureRelationAgentJSONAsBytes, _ := json.Marshal(featureRelationAgent)

	// === Save marble to state ===
	stub.PutState(relationId, featureRelationAgentJSONAsBytes)

	return featureRelationAgent, nil
}

// =====================================================================================================================
// Create Feature Based IndexFeatureRelationAgent - to do query based on Feature
// =====================================================================================================================
func CreateFeatureIndexFeatureRelationAgent(featureRelationAgent *FeatureRelationAgent, stub shim.ChaincodeStubInterface) (featureAgentIndexFeatureRelationAgentKey string, err error) {
	//  ==== IndexFeatureRelationAgent the featureAgentRelation to enable feature-based range queries, e.g. return all x features ====
	//  An 'index' is a normal key/value entry in state.
	//  The key is a composite key, with the elements that you want to range query on listed first.
	//  In our case, the composite key is based on feature~agent~relation.
	//  This will enable very efficient state range queries based on composite keys matching feature~agent~relation
	indexName := "feature~agent~relation"
	featureAgentIndexFeatureRelationAgentKey, err = stub.CreateCompositeKey(indexName, []string{featureRelationAgent.FeatureId, featureRelationAgent.AgentId, featureRelationAgent.RelationId})
	if err != nil {
		return featureAgentIndexFeatureRelationAgentKey, err
	}
	return featureAgentIndexFeatureRelationAgentKey, nil
}

// =====================================================================================================================
// Create Agent Based IndexFeatureRelationAgent - to do query based on Agent
// =====================================================================================================================
func CreateAgentIndexFeatureRelationAgent(featureRelationAgent *FeatureRelationAgent, stub shim.ChaincodeStubInterface) (agentFeatureIndexFeatureRelationAgent string, err error) {
	//  ==== IndexFeatureRelationAgent the featureAgentRelation to enable feature-based range queries, e.g. return all x agents ====
	//  An 'index' is a normal key/value entry in state.
	//  The key is a composite key, with the elements that you want to range query on listed first.
	//  In our case, the composite key is based on agent~feature~relation.
	//  This will enable very efficient state range queries based on composite keys matching agent~feature~relation
	indexName := "agent~feature~relation"
	agentFeatureIndexFeatureRelationAgent, err = stub.CreateCompositeKey(indexName, []string{featureRelationAgent.AgentId, featureRelationAgent.FeatureId, featureRelationAgent.RelationId})
	if err != nil {
		return agentFeatureIndexFeatureRelationAgent, err
	}
	return agentFeatureIndexFeatureRelationAgent, nil
}

// =====================================================================================================================
// CheckingCreatingIndexingFeatureRelationAgent - Incapsulate the three tasks:
// 1. CHECKING
// 2. CREATING
// 3. INDEXING
// =====================================================================================================================
func CheckingCreatingIndexingFeatureRelationAgent(featureId string, agentId string, cost string, time string, description string, stub shim.ChaincodeStubInterface) (*FeatureRelationAgent, error) {

	// ==== Check if featureRelationAgent already exists ====
	// TODO: Definire come creare relationId, per ora è composto dai due ID (featureId + agentId)
	relationId := featureId + agentId
	agent2AsBytes, err := stub.GetState(relationId)
	if err != nil {
		return nil, errors.New("Failed to get feature agent relation: " + err.Error())
	} else if agent2AsBytes != nil {
		featureLog.Info("This feature agent relation already exists with relationId: " + relationId)
		return nil, errors.New("This feature agent relation already exists with relationId: " + relationId)
	}

	// ==== Actual creation of featureRelationAgent  ====
	featureRelationAgent, err := CreateFeatureRelationAgent(relationId, featureId, agentId, cost, time, description, stub)
	if err != nil {
		return nil, errors.New("Failed to create feature agent relation of feature " + featureId + " with agent " + agentId)
	}

	// ==== Indexing of featureRelationAgent by Feature ====

	// index create
	featureAgentIndexFeatureRelationAgentKey, featureIndexFeatureRelationAgentError := CreateFeatureIndexFeatureRelationAgent(featureRelationAgent, stub)
	if featureIndexFeatureRelationAgentError != nil {
		return nil, errors.New(featureIndexFeatureRelationAgentError.Error())
	}
	//  Note - passing a 'nil' emptyValue will effectively delete the key from state, therefore we pass null character as emptyValue
	//  Save index entry to state. Only the key Name is needed, no need to store a duplicate copy of the FeatureRelationAgent.
	emptyValue := []byte{0x00}
	// index save
	putStateError := stub.PutState(featureAgentIndexFeatureRelationAgentKey, emptyValue)
	if putStateError != nil {
		return nil, errors.New("Error  saving Feature index: " + putStateError.Error())
	}

	// ==== Indexing of featureRelationAgent by Agent ====

	// index create
	agentFeatureIndexFeatureRelationAgentKey, agentIndexFeatureRelationAgentError := CreateAgentIndexFeatureRelationAgent(featureRelationAgent, stub)
	if agentIndexFeatureRelationAgentError != nil {
		return nil, errors.New(agentIndexFeatureRelationAgentError.Error())
	}
	// index save
	putStateAgentIndexFeatureRelationAgentError := stub.PutState(agentFeatureIndexFeatureRelationAgentKey, emptyValue)
	if putStateAgentIndexFeatureRelationAgentError != nil {
		return nil, errors.New("Error  saving Agent index: " + putStateAgentIndexFeatureRelationAgentError.Error())
	}

	return featureRelationAgent, nil
}

// =====================================================================================================================
// UpdateFeatureRelationAgentCost - Modify the featureRelationAgent cost of the asset passed as parameter
// =====================================================================================================================
func UpdateFeatureRelationAgentCost(featureRelationAgent FeatureRelationAgent, newRelationCost string, stub shim.ChaincodeStubInterface) error {

	featureRelationAgent.Cost = newRelationCost

	featureRelationAgentAsBytes, _ := json.Marshal(featureRelationAgent)
	putStateError := stub.PutState(featureRelationAgent.RelationId, featureRelationAgentAsBytes)
	if putStateError != nil {
		featureRelationAgentLog.Error(putStateError)
		return errors.New(putStateError.Error())
	}
	return nil
}

// =====================================================================================================================
// UpdateFeatureRelationAgentTime - Modify the featureRelationAgent time of the asset passed as parameter
// =====================================================================================================================
func UpdateFeatureRelationAgentTime(featureRelationAgent FeatureRelationAgent, newRelationTime string, stub shim.ChaincodeStubInterface) error {

	featureRelationAgent.Time = newRelationTime

	featureRelationAgentAsBytes, _ := json.Marshal(featureRelationAgent)
	putStateError := stub.PutState(featureRelationAgent.RelationId, featureRelationAgentAsBytes)
	if putStateError != nil {
		featureRelationAgentLog.Error(putStateError)
		return errors.New(putStateError.Error())
	}
	return nil
}

// =====================================================================================================================
// UpdateFeatureRelationAgentDescription - Modify the featureRelationAgent description of the asset passed as parameter
// =====================================================================================================================
func UpdateFeatureRelationAgentDescription(featureRelationAgent FeatureRelationAgent, newRelationTime string, stub shim.ChaincodeStubInterface) error {

	featureRelationAgent.Description = newRelationTime

	featureRelationAgentAsBytes, _ := json.Marshal(featureRelationAgent)
	putStateError := stub.PutState(featureRelationAgent.RelationId, featureRelationAgentAsBytes)
	if putStateError != nil {
		featureRelationAgentLog.Error(putStateError)
		return errors.New(putStateError.Error())
	}
	return nil
}

// ============================================================================================================================
// Get Feature Agent Relation - get the feature agent relation asset from ledger - return (nil,nil) if not found
// ============================================================================================================================
func GetFeatureRelationAgent(stub shim.ChaincodeStubInterface, relationId string) (FeatureRelationAgent, error) {
	var featureRelationAgent FeatureRelationAgent
	featureRelationAgentAsBytes, err := stub.GetState(relationId) //getState retreives a key/value from the ledger
	if err != nil {                                               //this seems to always succeed, even if key didn't exist
		return featureRelationAgent, errors.New("Error in finding feature relation with agent: " + err.Error())
	}

	json.Unmarshal(featureRelationAgentAsBytes, &featureRelationAgent) //un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo FeatureRelationAgent)

	return featureRelationAgent, nil
}

// =====================================================================================================================
// Get Feature Agent Relation Not Found Error - get the feature agent relation asset from ledger - throws error if not found (error!=nil ---> key not found)
// =====================================================================================================================
func GetFeatureRelationAgentNotFoundError(stub shim.ChaincodeStubInterface, relationId string) (FeatureRelationAgent, error) {
	var featureRelationAgent FeatureRelationAgent
	featureRelationAgentAsBytes, err := stub.GetState(relationId) //getState retreives a key/value from the ledger
	if err != nil {                                               //this seems to always succeed, even if key didn't exist
		return featureRelationAgent, errors.New("Error in finding feature relation with agent: " + err.Error())
	}

	if featureRelationAgentAsBytes == nil {
		return FeatureRelationAgent{}, errors.New("FeatureRelationAgent non found, RelationId: " + relationId)
	}
	json.Unmarshal(featureRelationAgentAsBytes, &featureRelationAgent) //un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo FeatureRelationAgent)

	return featureRelationAgent, nil
}

// =====================================================================================================================
// Get the feature query on FeatureRelationAgent - Execute the query based on feature composite index
// =====================================================================================================================
func GetFeatureRelationAgentByFeature(featureId string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the feature~agent~relation index by feature
	// This will execute a key range query on all keys starting with 'feature'
	featureAgentResultsIterator, err := stub.GetStateByPartialCompositeKey("feature~agent~relation", []string{featureId})
	if err != nil {
		return featureAgentResultsIterator, err
	}
	defer featureAgentResultsIterator.Close()
	return featureAgentResultsIterator, nil
}

// =====================================================================================================================
// Get the agent query on FeatureRelationAgent - Execute the query based on agent composite index
// =====================================================================================================================
// TODO: BUG, ritorna anche il record dell'agente
func GetFeatureRelationAgentByAgent(agentId string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the feature~agent~relation index by feature
	// This will execute a key range query on all keys starting with 'agent'
	agentFeatureResultsIterator, err := stub.GetStateByPartialCompositeKey("agent~feature~relation", []string{agentId})
	if err != nil {
		return agentFeatureResultsIterator, err
	}
	defer agentFeatureResultsIterator.Close()
	return agentFeatureResultsIterator, nil
}

// =====================================================================================================================
// Delete Feature Relation Agent - delete from state DelState() - "removing"" a key/value from the ledger
// =====================================================================================================================
func DeleteFeatureRelationAgent(stub shim.ChaincodeStubInterface, relationId string) error {
	// remove the featureRelationAgent
	err := stub.DelState(relationId) //remove the key from chaincode state
	if err != nil {
		return err
	}
	return nil
}

// =====================================================================================================================
// Delete Feature IndexFeatureRelationAgent - Delete the index
// =====================================================================================================================
func DeleteFeatureIndexFeatureRelationAgent(stub shim.ChaincodeStubInterface, indexName string, featureId string, agentId string, relationId string) error {

	agentFeatureIndexFeatureRelationAgent, err := stub.CreateCompositeKey(indexName, []string{featureId, agentId, relationId})
	if err != nil {
		return err
	}
	err = stub.DelState(agentFeatureIndexFeatureRelationAgent) //remove the key from chaincode state
	if err != nil {
		return err
	}
	return nil
}

// =====================================================================================================================
// Delete Agent Feature Relation - delete from state and from marble index Shows Off DelState() - "removing"" a key/value from the ledger
// =====================================================================================================================
func DeleteAgentIndexFeatureRelationAgent(stub shim.ChaincodeStubInterface, indexName string, agentId string, featureId string, relationId string) error {
	// remove the featureRelationAgent
	agentFeatureIndexFeatureRelationAgent, err := stub.CreateCompositeKey(indexName, []string{agentId, featureId, relationId})
	if err != nil {
		return err
	}
	err = stub.DelState(agentFeatureIndexFeatureRelationAgent) //remove the key from chaincode state
	if err != nil {
		return err
	}
	return nil
}

// =====================================================================================================================
// GetAgentSliceFromByFeatureQuery - Get the Agent and FeatureRelationAgent Slices from the result of query "byFeature"
// =====================================================================================================================
func GetFeatureRelationSliceFromRangeQuery(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) ([]FeatureRelationAgent, error) {
	var featureRelationAgentSlice []FeatureRelationAgent
	// get the feature agent relation from feature~agent~relation composite key
	// defer queryIterator.Close()

	for i := 0; queryIterator.HasNext(); i++ {
		responseRange, err := queryIterator.Next()
		if err != nil {
			return nil, err
		}
		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		relationId := compositeKeyParts[2]

		ifeatureRelationAgent, err := GetFeatureRelationAgentNotFoundError(stub, relationId)
		featureRelationAgentSlice = append(featureRelationAgentSlice, ifeatureRelationAgent)
		if err != nil {
			return nil, err
		}
		fmt.Printf("- found a relation RELATION ID: %s \n", relationId)
	}
	queryIterator.Close()
	return featureRelationAgentSlice, nil
}

// =====================================================================================================================
// GetAgentSliceFromByFeatureQuery - Get the Agent Slice from the result of query "byFeature"
// =====================================================================================================================
func GetAgentSliceFromByFeatureQuery(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) ([]Agent, error) {
	var agentSlice []Agent
	for i := 0; queryIterator.HasNext(); i++ {
		// Note that we don't get the value (2nd return variable), we'll just get the marble Name from the composite key
		responseRange, err := queryIterator.Next()
		if err != nil {
			return nil, err
		}
		// get the feature agent relation from feature~agent~relation composite key
		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		agentId := compositeKeyParts[1]

		iAgent, err := GetAgentNotFoundError(stub, agentId)
		agentSlice = append(agentSlice, iAgent)

		if err != nil {
			return nil, err
		}
	}
	queryIterator.Close()
	return agentSlice, nil
}

// =====================================================================================================================
// Print Results Iterator - Print on screen the general iterator of the composite index query result
// =====================================================================================================================
func PrintByFeatureResultsIterator(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) error {
	for i := 0; queryIterator.HasNext(); i++ {
		// Note that we don't get the value (2nd return variable), we'll just get the marble Name from the composite key
		responseRange, err := queryIterator.Next()
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
		fmt.Printf("- found a relation from OBJECT_TYPE:%s SERVICE ID:%s AGENT ID:%s RELATION ID: %s\n", objectType, featureId, agentId, relationId)
	}
	return nil
}

// =====================================================================================================================
// Print Results Iterator - Print on screen the general iterator of the composite index query result
// =====================================================================================================================
func PrintByAgentResultsIteratorFeatureRelationAgent(iteratorInterface shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) error {
	for i := 0; iteratorInterface.HasNext(); i++ {
		// Note that we don't get the value (2nd return variable), we'll just get the marble Name from the composite key
		responseRange, err := iteratorInterface.Next()
		if err != nil {
			return err
		}
		// get the feature agent relation from feature~agent~relation composite key
		objectType, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		agentId := compositeKeyParts[0]
		featureId := compositeKeyParts[1]
		relationId := compositeKeyParts[2]

		if err != nil {
			return err
		}
		fmt.Printf("- found a relation from OBJECT_TYPE:%s AGENT ID:%s SERVICE ID:%s  RELATION ID: %s\n", objectType, agentId, featureId, relationId)
	}
	return nil
}

// =====================================================================================================================
// Print Results Iterator - Print on screen the general iterator of the composite index query result
// =====================================================================================================================
func PrintResultsIteratorFeatureRelationAgent(iteratorInterface shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) error {
	for i := 0; iteratorInterface.HasNext(); i++ {
		// Note that we don't get the value (2nd return variable), we'll just get the marble Name from the composite key
		responseRange, err := iteratorInterface.Next()
		if err != nil {
			return err
		}
		// get the feature agent relation from feature~agent~relation composite key
		// get the agent feature relation from agent~feature~relation composite key
		objectType, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)
		if err != nil {
			return err
		}
		fmt.Printf("- found a relation from OBJECT_TYPE:%s SERVICE ID:%s AGENT ID:%s RELATION ID: %s\n", objectType, compositeKeyParts[0], compositeKeyParts[1], compositeKeyParts[2])
	}
	return nil
}
