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

var innMinReputationLog = shim.NewLogger("innMinReputation")

// =====================================================================================================================
// Define the Agent's InnMindReputation structure
// =====================================================================================================================
// - InnMindReputationId
// - AgentId
// - FeatureId
// - AgentRole
// - Value
// UNIVOCAL: AgentId, FeatureId, AgentRole

type InnMindReputation struct {
	// innMinReputationId = agentId + serviceId + agentRole
	InnMindReputationId string `json:"InnMindReputationId"`
	AgentId             string `json:"AgentId"`
	FeatureId           string `json:"FeatureId"`
	AgentRole           string `json:"AgentRole"` // "STARTUP" || "EXPERT"
	Value               string `json:"Value"`     // Value of InnMindReputation of the agent
}

// AgentRole Values
const (
	Startup = "STARTUP"
	Expert  = "EXPERT"
)

//TODO: Don't delete innMinReputation of a deleted agent (I don't care because I can use directly the blockchain?)

// =====================================================================================================================
// createInnMindReputation - create a new innMinReputation identified as: service-agent-agentrole (Startup || Expert)
// =====================================================================================================================
func CreateInnMindReputation(innMinReputationId string, agentId string, serviceId string, agentRole string, value string, stub shim.ChaincodeStubInterface) (*InnMindReputation, error) {
	// agentRoleNow := "Startup"
	// ==== Create marble object and marshal to JSON ====
	innMinReputation := &InnMindReputation{innMinReputationId, agentId, serviceId, agentRole, value}
	InnMindReputationJSONAsBytes, _ := json.Marshal(innMinReputation)

	// === Save marble to state ===
	stub.PutState(innMinReputationId, InnMindReputationJSONAsBytes)

	return innMinReputation, nil
}

// =====================================================================================================================
// Create Agent Based Index - to do query based on Agent, Feature and AgentRole
// =====================================================================================================================
func CreateAgentFeatureRoleIndex(innMinReputation *InnMindReputation, stub shim.ChaincodeStubInterface) (agentFeatureRoleIndex string, err error) {
	//  ==== Index the serviceAgentRelation to enable service-based range queries, e.g. return all x agents ====
	//  An 'index' is a normal key/value entry in state.
	//  The key is a composite key, with the elements that you want to range query on listed first.
	//  In our case, the composite key is based on agent~service~relation.
	//  This will enable very efficient state range queries based on composite keys matching agent~service~relation
	indexName := "agent~service~agentRole~innMinReputation"
	agentFeatureRoleIndex, err = stub.CreateCompositeKey(indexName, []string{innMinReputation.AgentId, innMinReputation.FeatureId, innMinReputation.AgentRole, innMinReputation.InnMindReputationId})
	if err != nil {
		return agentFeatureRoleIndex, err
	}
	return agentFeatureRoleIndex, nil
}

// =====================================================================================================================
// Create Agent Based Index - to do query based on AgentRole, Agent, Feature
// =====================================================================================================================
func CreateRoleAgentFeatureIndex(innMinReputation *InnMindReputation, stub shim.ChaincodeStubInterface) (agentFeatureRoleIndex string, err error) {
	//  ==== Index the serviceAgentRelation to enable service-based range queries, e.g. return all x agents ====
	//  An 'index' is a normal key/value entry in state.
	//  The key is a composite key, with the elements that you want to range query on listed first.
	//  In our case, the composite key is based on agent~service~relation.
	//  This will enable very efficient state range queries based on composite keys matching agent~service~relation
	indexName := "agentRole~agent~service~innMinReputation"
	agentFeatureRoleIndex, err = stub.CreateCompositeKey(indexName, []string{innMinReputation.AgentRole, innMinReputation.AgentId, innMinReputation.FeatureId, innMinReputation.InnMindReputationId})
	if err != nil {
		return agentFeatureRoleIndex, err
	}
	return agentFeatureRoleIndex, nil
}

// =====================================================================================================================
// CheckingCreatingIndexingInnMindReputation - Incapsulate the three tasks:
// 1. CHECKING
// 2. CREATING
// 3. INDEXING
// =====================================================================================================================
func CheckingCreatingIndexingInnMindReputation(agentId string, serviceId string, agentRole string, value string, stub shim.ChaincodeStubInterface) (*InnMindReputation, error) {
	// ==== Check if AgentRole == "STARTUP" || "EXPERT" ====
	if Startup != agentRole && Expert != agentRole {
		return nil, errors.New("Wrong Agent Role: " + agentRole + ", use \"" + Startup + "\"or \"" + Expert + "\"")
	}

	// ==== Check if innMinReputation already exists ====
	// TODO: Definire come creare innMinReputationId, per ora è composto dai tre ID (agentId + serviceId + agentRole)
	innMinReputationId := agentId + serviceId + agentRole
	innMinReputationAsBytes, err := stub.GetState(innMinReputationId)
	if err != nil {
		return nil, errors.New("Failed to get service agent innMinReputation: " + err.Error())
	} else if innMinReputationAsBytes != nil {
		serviceLog.Info("This service agent innMinReputation already exists with innMinReputationId: " + innMinReputationId)
		return nil, errors.New("This service agent innMinReputation already exists with innMinReputationId: " + innMinReputationId)
	}

	// ==== Actual creation of InnMindReputation  ====
	innMinReputation, err := CreateInnMindReputation(innMinReputationId, agentId, serviceId, agentRole, value, stub)
	if err != nil {
		return nil, errors.New("Failed to create innMinReputation of  agent  " + agentId + " relation of service " + serviceId)
	}

	// ==== Indexing of innMinReputation by Agent ====

	// index create
	agentInnMindReputationIndex, serviceIndexError := CreateAgentFeatureRoleIndex(innMinReputation, stub)
	if serviceIndexError != nil {
		return nil, errors.New(serviceIndexError.Error())
	}
	//  Note - passing a 'nil' emptyValue will effectively delete the key from state, therefore we pass null character as emptyValue
	//  Save index entry to state. Only the key Name is needed, no need to store a duplicate copy of the FeatureAgentRelation.
	emptyValue := []byte{0x00}
	// index save
	putStateError := stub.PutState(agentInnMindReputationIndex, emptyValue)
	if putStateError != nil {
		return nil, errors.New("Error saving Agent InnMindReputation index: " + putStateError.Error())
	}

	// ==== Indexing of innMinReputation by AgentRole ====

	// index create
	agentRoleInnMindReputationIndex, serviceIndexError := CreateRoleAgentFeatureIndex(innMinReputation, stub)
	if serviceIndexError != nil {
		return nil, errors.New(serviceIndexError.Error())
	}

	// index save
	putStateError = stub.PutState(agentRoleInnMindReputationIndex, emptyValue)
	if putStateError != nil {
		return nil, errors.New("Error saving AgentRole InnMindReputation index: " + putStateError.Error())
	}


	return innMinReputation, nil
}

// =====================================================================================================================
// CheckingCreatingIndexingInnMindReputation - Incapsulate the three tasks:
// 1. CHECKING
// 2. UPDATING || CREATING
// 3. INDEXING
// =====================================================================================================================
func CheckingUpdatingOrCreatingIndexingInnMindReputation(agentId string, serviceId string, agentRole string, value string, stub shim.ChaincodeStubInterface) (*InnMindReputation, error) {
	// ==== Check if AgentRole == Startup || Expert ====
	if Startup != agentRole && Expert != agentRole {
		return nil, errors.New("Wrong Agent Role: " + agentRole + ", use \"" + Startup + "\"or \"" + Expert + "\"")
	}

	var innMinReputation InnMindReputation

	// ==== Check if innMinReputation already exists ====
	// TODO: Definire come creare innMinReputationId, per ora è composto dai tre ID (agentId + serviceId + agentRole)
	innMinReputationId := agentId + serviceId + agentRole
	innMinReputationAsBytes, err := stub.GetState(innMinReputationId)

	if err != nil {
		return nil, errors.New("Failed to get service agent innMinReputation: " + err.Error())
	} else if innMinReputationAsBytes != nil {
		// ==== InnMindReputation Already exist, modify it  ====
		error := json.Unmarshal(innMinReputationAsBytes, &innMinReputation)
		if error != nil {
			return nil, errors.New(error.Error())
		}

		modifyError := ModifyInnMindReputationValue(innMinReputation, value, stub)
		if modifyError != nil {
			return nil, errors.New("Error modifying innMinReputation: " + modifyError.Error())
		}
	} else {

		// ==== Actual creation of InnMindReputation  ====
		innMinReputation, err := CreateInnMindReputation(innMinReputationId, agentId, serviceId, agentRole, value, stub)
		if err != nil {
			return nil, errors.New("Failed to create innMinReputation of  agent  " + agentId + " relation of service " + serviceId)
		}

		// ==== Indexing of innMinReputation by Feature Tx Id ====

		// index create
		agentInnMindReputationIndex, serviceIndexError := CreateAgentFeatureRoleIndex(innMinReputation, stub)
		if serviceIndexError != nil {
			return nil, errors.New(serviceIndexError.Error())
		}
		//  Note - passing a 'nil' emptyValue will effectively delete the key from state, therefore we pass null character as emptyValue
		//  Save index entry to state. Only the key Name is needed, no need to store a duplicate copy of the FeatureAgentRelation.
		emptyValue := []byte{0x00}
		// index save
		putStateError := stub.PutState(agentInnMindReputationIndex, emptyValue)
		if putStateError != nil {
			return nil, errors.New("Error saving Agent InnMindReputation index: " + putStateError.Error())
		}

		// ==== Indexing of innMinReputation by AgentRole ====

		// index create
		agentRoleInnMindReputationIndex, serviceIndexError := CreateRoleAgentFeatureIndex(innMinReputation, stub)
		if serviceIndexError != nil {
			return nil, errors.New(serviceIndexError.Error())
		}

		// index save
		putStateError = stub.PutState(agentRoleInnMindReputationIndex, emptyValue)
		if putStateError != nil {
			return nil, errors.New("Error saving AgentRole InnMindReputation index: " + putStateError.Error())
		}
	}

	return &innMinReputation, nil
}

// =====================================================================================================================
// modifyInnMindReputationValue - Modify the innMinReputation value of the asset passed as parameter (aka UPDATE InnMindReputation.Value)
// =====================================================================================================================
func ModifyInnMindReputationValue(innMinReputation InnMindReputation, newInnMindReputationValue string, stub shim.ChaincodeStubInterface) error {

	innMinReputation.Value = newInnMindReputationValue

	innMinReputationAsBytes, _ := json.Marshal(innMinReputation)
	putStateError := stub.PutState(innMinReputation.InnMindReputationId, innMinReputationAsBytes)
	if putStateError != nil {
		return errors.New(putStateError.Error())
	}

	return nil
}

// =====================================================================================================================
// Get InnMindReputation - get the innMinReputation asset from ledger - return (nil,nil) if not found
// =====================================================================================================================
func GetInnMindReputation(stub shim.ChaincodeStubInterface, innMinReputationId string) (InnMindReputation, error) {
	var innMinReputation InnMindReputation
	innMinReputationAsBytes, err := stub.GetState(innMinReputationId) //getState retreives a key/value from the ledger
	if err != nil {                                                   //this seems to always succeed, even if key didn't exist
		return innMinReputation, errors.New("Error in finding the innMinReputation of the agent: " + err.Error())
	}

	json.Unmarshal(innMinReputationAsBytes, &innMinReputation) //un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo FeatureRelationAgent)

	return innMinReputation, nil
}

// =====================================================================================================================
// Get InnMindReputation Not Found Error - get the innMinReputation asset from ledger - throws error if not found (error!=nil ---> key not found)
// =====================================================================================================================
func GetInnMindReputationNotFoundError(stub shim.ChaincodeStubInterface, innMinReputationId string) (InnMindReputation, error) {
	var innMinReputation InnMindReputation
	serviceRelationAgentAsBytes, err := stub.GetState(innMinReputationId) //getState retreives a key/value from the ledger
	if err != nil {                                                       //this seems to always succeed, even if key didn't exist
		return innMinReputation, errors.New("Error in finding service relation with agent: " + err.Error())
	}

	if serviceRelationAgentAsBytes == nil {
		return InnMindReputation{}, errors.New("Feature non found, FeatureId: " + innMinReputationId)
	}
	json.Unmarshal(serviceRelationAgentAsBytes, &innMinReputation) //un stringify it aka JSON.parse()

	// TODO: Inserire controllo di tipo (Verificare sia di tipo FeatureRelationAgent)

	return innMinReputation, nil
}

// =====================================================================================================================
// Get the service query on FeatureRelationAgent - Execute the query based on service composite index
// =====================================================================================================================
func GetByAgentFeatureRole(agentId string, serviceId string, agentRole string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the service~agent~relation index by service
	// This will execute a key range query on all keys starting with 'service'
	indexName := "agent~service~agentRole~innMinReputation"

	serviceAgentResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{agentId, serviceId, agentRole})
	if err != nil {
		return serviceAgentResultsIterator, err
	}
	return serviceAgentResultsIterator, nil
}

// =====================================================================================================================
// Get the service query on FeatureRelationAgent - Execute the query based on service composite index
// =====================================================================================================================
func GetByAgentFeature(agentId string, serviceId string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the service~agent~relation index by service
	// This will execute a key range query on all keys starting with 'service'
	indexName := "agent~service~agentRole~innMinReputation"

	serviceAgentResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{agentId, serviceId})
	if err != nil {
		return serviceAgentResultsIterator, err
	}
	return serviceAgentResultsIterator, nil
}

// =====================================================================================================================
// Get the service query on FeatureRelationAgent - Execute the query based on service composite index
// =====================================================================================================================
func GetByAgentOnlyInnMind(agentId string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the service~agent~relation index by service
	// This will execute a key range query on all keys starting with 'service'
	indexName := "agent~service~agentRole~innMinReputation"

	serviceAgentResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{agentId})
	if err != nil {
		return serviceAgentResultsIterator, err
	}
	return serviceAgentResultsIterator, nil
}

// =====================================================================================================================
// Get the service query on FeatureRelationAgent - Execute the query based on service composite index
// =====================================================================================================================
func GetByRoleAgentFeature(agentRole string, agentId string, featureId string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the service~agent~relation index by service
	// This will execute a key range query on all keys starting with 'service'
	indexName := "agentRole~agent~service~innMinReputation"

	serviceAgentResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{agentRole, agentId,  featureId})
	if err != nil {
		return serviceAgentResultsIterator, err
	}
	return serviceAgentResultsIterator, nil
}

// =====================================================================================================================
// Get the service query on FeatureRelationAgent - Execute the query based on service composite index
// =====================================================================================================================
func GetByRoleAgent(agentRole string, featureId string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the service~agent~relation index by service
	// This will execute a key range query on all keys starting with 'service'
	indexName := "agentRole~agent~service~innMinReputation"

	serviceAgentResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{agentRole, featureId})
	if err != nil {
		return serviceAgentResultsIterator, err
	}
	return serviceAgentResultsIterator, nil
}

// =====================================================================================================================
// Get the service query on FeatureRelationAgent - Execute the query based on service composite index
// =====================================================================================================================
func GetByRoleOnlyInnMind(agentRole string, stub shim.ChaincodeStubInterface) (shim.StateQueryIteratorInterface, error) {
	// Query the service~agent~relation index by service
	// This will execute a key range query on all keys starting with 'service'
	indexName := "agentRole~agent~service~innMinReputation"

	serviceAgentResultsIterator, err := stub.GetStateByPartialCompositeKey(indexName, []string{agentRole})
	if err != nil {
		return serviceAgentResultsIterator, err
	}
	return serviceAgentResultsIterator, nil
}

// =====================================================================================================================
// Delete InnMindReputation - "removing"" a key/value from the ledger
// =====================================================================================================================
func DeleteInnMindReputation(stub shim.ChaincodeStubInterface, innMinReputationId string) error {
	// remove the serviceRelationAgent
	err := stub.DelState(innMinReputationId) //remove the key from chaincode state
	if err != nil {
		return err
	}
	return nil
}

// =====================================================================================================================
// Delete Feature Agent Role InnMindReputation - "removing"" the key/value from the ledger relative to the index
// =====================================================================================================================
func DeleteAgentFeatureRoleIndex(stub shim.ChaincodeStubInterface, indexName string, agentId string, serviceId string, agentRole string, innMinReputationId string) error {
	// remove the serviceRelationAgent
	agentFeatureRoleIndex, err := stub.CreateCompositeKey(indexName, []string{agentId, serviceId, agentRole, innMinReputationId})
	if err != nil {
		return err
	}
	err = stub.DelState(agentFeatureRoleIndex) //remove the keySTARTUP from chaincode state
	if err != nil {
		return err
	}
	return nil
}

// =====================================================================================================================
// Delete Role Agent Feature  InnMindReputation - "removing"" the key/value from the ledger relative to the index
// =====================================================================================================================
func DeleteRoleAgentFeatureIndex(stub shim.ChaincodeStubInterface, indexName string, agentId string, serviceId string, agentRole string, innMinReputationId string) error {
	// remove the serviceRelationAgent
	roleAgentFeatureIndex, err := stub.CreateCompositeKey(indexName, []string{agentRole, agentId, serviceId,  innMinReputationId})
	if err != nil {
		return err
	}
	err = stub.DelState(roleAgentFeatureIndex) //remove the keySTARTUP from chaincode state
	if err != nil {
		return err
	}
	return nil
}

// ============================================================================================================================
// GetAgentSliceFromByFeatureQuery - Get the Agent and FeatureRelationAgent Slices from the result of query "byFeature"
// ============================================================================================================================
func GetInnMindReputationSliceFromRangeQuery(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) ([]InnMindReputation, error) {
	var serviceRelationAgentSlice []InnMindReputation
	defer queryIterator.Close()

	for i := 0; queryIterator.HasNext(); i++ {
		responseRange, err := queryIterator.Next()
		if err != nil {
			return nil, err
		}
		_, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		innMinReputationId := compositeKeyParts[3]

		iserviceRelationAgent, err := GetInnMindReputation(stub, innMinReputationId)
		serviceRelationAgentSlice = append(serviceRelationAgentSlice, iserviceRelationAgent)
		if err != nil {
			return nil, err
		}
		fmt.Printf("- found a innMinReputation REPUTATION ID: %s \n", innMinReputationId)
	}
	return serviceRelationAgentSlice, nil
}

// ============================================================================================================================
// Print Results Iterator - Print on screen the general iterator of the composite index query result
// ============================================================================================================================
func PrintByAgentFeatureRoleInnMindReputationResultsIterator(queryIterator shim.StateQueryIteratorInterface, stub shim.ChaincodeStubInterface) error {
	defer queryIterator.Close()
	for i := 0; queryIterator.HasNext(); i++ {
		// Note that we don't get the value (2nd return variable), we'll just get the marble Name from the composite key
		responseRange, err := queryIterator.Next()
		if err != nil {
			return err
		}
		// get the service agent relation from service~agent~relation composite key
		objectType, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)

		agentId := compositeKeyParts[0]
		serviceId := compositeKeyParts[1]
		agentRole := compositeKeyParts[2]
		innMinReputationId := compositeKeyParts[3]

		if err != nil {
			return err
		}
		fmt.Printf("- found a relation from OBJECT_TYPE:%s AGENT ID:%s SERVICE ID:%s AGENT ROLE: %s REPUTATION ID: %s\n", objectType, agentId, serviceId, agentRole, innMinReputationId)
	}
	return nil
}
