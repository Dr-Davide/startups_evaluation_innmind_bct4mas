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
	// a "github.com/pavva91/trustreputationInnMindledger/assets"
	a "github.com/pavva91/assets"
)

var reputationInnMindInvokeCallLog = shim.NewLogger("reputationInnMindInvokeCall")

/*
For now we want that the Activity assets can only be added on the ledger (NO MODIFY, NO DELETE)
*/
// ========================================================================================================================
// Create Reviewed Feature Evaluation - wrapper of CreateFeatureRelationAgentAndInnMindReputation called from chiancode's Invoke
// ========================================================================================================================
func CreateInnMindReputation(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0         1            2             3
	// "AgentId", "FeatureId", "AgentRole", "Value"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 4)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	agentId := args[0]
	featureId := args[1]
	agentRole := args[2]
	value := args[3]

	// ==== Check if already existing agent ====
	agent, errA := a.GetAgentNotFoundError(stub, agentId)
	if errA != nil {
		reputationInnMindInvokeCallLog.Error(errA.Error())
		return shim.Error("Failed to find Agent by id: " + errA.Error())
	}

	// ==== Check if already existing feature ====
	feature, errS := a.GetFeatureNotFoundError(stub, featureId)
	if errS != nil {
		reputationInnMindInvokeCallLog.Error(errS.Error())
		return shim.Error("Failed to find feature by id " + errS.Error())
	}

	// ==== Actual checking, creation and indexing of InnMindReputation  ====
	reputationInnMind, err := a.CheckingCreatingIndexingInnMindReputation(agentId, featureId, agentRole, value, stub)
	if err != nil {
		reputationInnMindInvokeCallLog.Error("Failed to create reputationInnMind of agent " + agent.Name + " of feature: " + feature.Name + " with agent role: " + agentRole + ": " + err.Error())
		return shim.Error("Failed to create reputationInnMind of agent " + agent.Name + " of feature: " + feature.Name + " with agent role: " + agentRole + ": " + err.Error())
	}

	// ==== InnMindReputation saved and indexed. Set Event ====

	eventPayload := "Created InnMindReputation: " + reputationInnMind.InnMindReputationId + " of agent ID: " + reputationInnMind.AgentId + ", for feature ID: " + reputationInnMind.FeatureId + ", with role: " + reputationInnMind.AgentRole
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("InnMindReputationCreatedEvent", payloadAsBytes)
	if eventError != nil {
		reputationInnMindInvokeCallLog.Error(eventError.Error())
		return shim.Error(eventError.Error())
	} else {
		reputationInnMindInvokeCallLog.Info("Event Create InnMindReputation OK")
	}

	// ==== InnMindReputation saved & indexed. Return success ====
	reputationInnMindInvokeCallLog.Info("InnMindReputationId: " + reputationInnMind.InnMindReputationId + " of agent: " + reputationInnMind.AgentId + " in role of: " + reputationInnMind.AgentRole + " relative to the feature: " + reputationInnMind.FeatureId)
	return shim.Success(nil)
}

// ========================================================================================================================
// Modify reputationInnMind value (if not exist, create it)
// ========================================================================================================================
func ModifyOrCreateInnMindReputationValue(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0         1            2             3
	// "AgentId", "FeatureId", "AgentRole", "Value"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 4)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		reputationInnMindInvokeCallLog.Error(sanitizeError.Error())
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	agentId := args[0]
	featureId := args[1]
	agentRole := args[2]
	value := args[3]

	// ==== Check if already existing agent ====
	agent, errA := a.GetAgentNotFoundError(stub, agentId)
	if errA != nil {
		reputationInnMindInvokeCallLog.Info("Failed to find Agent by id " + agentId)
		return shim.Error("Failed to find Agent by id: " + errA.Error())
	}

	// ==== Check if already existing feature ====
	feature, errS := a.GetFeatureNotFoundError(stub, featureId)
	if errS != nil {
		reputationInnMindInvokeCallLog.Info("Failed to find feature by id " + featureId)
		return shim.Error("Failed to find feature by id " + errS.Error())
	}

	// ==== Actual checking, modify (or creation and indexing if not exist before) of InnMindReputation  ====
	reputationInnMind, err := a.CheckingUpdatingOrCreatingIndexingInnMindReputation(agentId, featureId, agentRole, value, stub)
	if err != nil {
		return shim.Error("Failed to modify reputationInnMind of agent " + agent.Name + " of feature: " + feature.Name + " with agent role: " + agentRole + ": " + err.Error())
	}

	// ==== InnMindReputation modified (or saved & indexed). Return success ====
	reputationInnMindInvokeCallLog.Info("InnMindReputationId: " + reputationInnMind.InnMindReputationId + " of agent: " + reputationInnMind.AgentId + " in role of: " + reputationInnMind.AgentRole + " relative to the feature: " + reputationInnMind.FeatureId)
	return shim.Success(nil)

	// ==== InnMindReputation saved & indexed. Return success ====
	reputationInnMindInvokeCallLog.Info("InnMindReputationId: " + reputationInnMind.InnMindReputationId + " of agent: " + reputationInnMind.AgentId + " in role of: " + reputationInnMind.AgentRole + " relative to the feature: " + reputationInnMind.FeatureId)
	return shim.Success(nil)
}

// ========================================================================================================================
// Modify reputationInnMind value (if not exist, throw error)
// ========================================================================================================================
func ModifyInnMindReputationValue(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0              1
	// "reputationInnMindId", "newInnMindReputationValue"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 2)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	reputationInnMindId := args[0]
	newInnMindReputationValue := args[1]

	// ==== get the reputationInnMind ====
	reputationInnMind, getError := a.GetInnMindReputationNotFoundError(stub, reputationInnMindId)
	if getError != nil {
		reputationInnMindInvokeCallLog.Info("Failed to find reputationInnMind by id " + reputationInnMindId)
		return shim.Error(getError.Error())
	}

	// ==== modify the reputationInnMind ====
	modifyError := a.ModifyInnMindReputationValue(reputationInnMind, newInnMindReputationValue, stub)
	if modifyError != nil {
		reputationInnMindInvokeCallLog.Info("Failed to modify the reputationInnMind value: " + newInnMindReputationValue)
		return shim.Error(modifyError.Error())
	}

	// ==== InnMindReputation modified. Set Event ====

	eventPayload := "Modified InnMindReputation: " + reputationInnMind.InnMindReputationId + " of agent ID: " + reputationInnMind.AgentId + ", for feature ID: " + reputationInnMind.FeatureId + ", with role: " + reputationInnMind.AgentRole + ", with new value: " + newInnMindReputationValue
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("InnMindReputationModifiedEvent", payloadAsBytes)
	if eventError != nil {
		reputationInnMindInvokeCallLog.Info("Error in event Modification: " + eventError.Error())
	} else {
		reputationInnMindInvokeCallLog.Info("Event Modifify InnMindReputation OK")
	}

	return shim.Success(nil)
}

// ============================================================================================================================
// Query InnMindReputation - wrapper of GetInnMindReputation called from the chaincode invoke
// ============================================================================================================================
func QueryInnMindReputation(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "reputationInnMindId"
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

	reputationInnMindId := args[0]

	// ==== get the reputationInnMind ====
	reputationInnMind, err := a.GetInnMindReputation(stub, reputationInnMindId)
	if err != nil {
		reputationInnMindInvokeCallLog.Info("Failed to find reputationInnMind by id " + reputationInnMindId)
		return shim.Error(err.Error())
	} else {
		reputationInnMindInvokeCallLog.Info("InnMindReputation ID: " + reputationInnMind.InnMindReputationId + ", of Agent: " + reputationInnMind.AgentId + ", Agent Role: " + reputationInnMind.AgentRole + ", of the Feature: " + reputationInnMind.FeatureId + ", with the value: " + reputationInnMind.Value)
		// ==== Marshal the Get Feature Evaluation query result ====
		evaluationAsJSON, err := json.Marshal(reputationInnMind)
		if err != nil {
			return shim.Error(err.Error())
		}
		return shim.Success(evaluationAsJSON)
	}
}

// ============================================================================================================================
// Query InnMindReputation Not Found Error- wrapper of GetInnMindReputation called from the chaincode invoke with Not Found Error
// ============================================================================================================================
func QueryInnMindReputationNotFoundError(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "reputationInnMindId"
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

	reputationInnMindId := args[0]

	// ==== get the reputationInnMind ====
	reputationInnMind, err := a.GetInnMindReputationNotFoundError(stub, reputationInnMindId)
	if err != nil {
		reputationInnMindInvokeCallLog.Info("Failed to find reputationInnMind by id " + reputationInnMindId)
		return shim.Error(err.Error())
	} else {
		reputationInnMindInvokeCallLog.Info("InnMindReputation ID: " + reputationInnMind.InnMindReputationId + ", of Agent: " + reputationInnMind.AgentId + ", Agent Role: " + reputationInnMind.AgentRole + ", of the Feature: " + reputationInnMind.FeatureId)
		// ==== Marshal the Get Feature Evaluation query result ====
		evaluationAsJSON, err := json.Marshal(reputationInnMind)
		if err != nil {
			return shim.Error(err.Error())
		}
		return shim.Success(evaluationAsJSON)
	}
}

// ========================================================================================================================
// QueryByAgentFeatureRole - wrapper of GetByAgentFeatureRole called from chiancode's Invoke
// TODO: Per come è impostato l'id ora è "inutile", però in vista di refactor ID sarà utile
// ========================================================================================================================
func QueryByAgentFeatureRole(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0           1            2
	// "agentId", "featureId", "agentRole"
	argumentSizeError := arglib.ArgumentSizeLimitVerification(args, 3)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	agentId := args[0]

	var byAgentFeatureRoleQuery shim.StateQueryIteratorInterface
	var err error

	// ==== Run the byAgentFeatureRole query ====
	switch len(args) {
	case 3:
		featureId := args[1]
		agentRole := args[2]
		byAgentFeatureRoleQuery, err = a.GetByAgentFeatureRole(agentId, featureId, agentRole, stub)
		if err != nil {
			reputationInnMindInvokeCallLog.Info("Failed to get reputationInnMind for this agent: " + agentId + ", in this feature: " + featureId + ", in this role: " + agentRole)
			return shim.Error(err.Error())
		}
	case 2:
		featureId := args[1]
		byAgentFeatureRoleQuery, err = a.GetByAgentFeature(agentId, featureId, stub)
		if err != nil {
			reputationInnMindInvokeCallLog.Info("Failed to get reputationInnMind for this agent: " + agentId + ", in this feature: " + featureId)
			return shim.Error(err.Error())
		}
	case 1:
		byAgentFeatureRoleQuery, err = a.GetByAgentOnly(agentId, stub)
		if err != nil {
			reputationInnMindInvokeCallLog.Info("Failed to get reputationInnMind for this agent: " + agentId)
			return shim.Error(err.Error())
		}
	}

	// ==== Print the byFeature query result ====
	err = a.PrintByAgentFeatureRoleInnMindReputationResultsIterator(byAgentFeatureRoleQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

// =====================================================================================================================
// GetInnMindReputationsByAgentFeatureRole - wrapper of GetByAgentFeatureRole called from chiancode's Invoke,
// for looking for featureEvaluations of a certain Agent-Feature-AgentRole triple
// return: InnMindReputations As JSON (IS EVERYTHING is WORKING is only ONE InnMindReputation the result)
// =====================================================================================================================
func GetInnMindReputationsByAgentFeatureRole(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0        1            2
	// "agentId", "featureId", "agentRole"
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

	agentId := args[0]
	featureId := args[1]
	agentRole := args[2]

	// TODO: With empty string works (FIX)
	indexName := "agent~feature~agentRole~reputationInnMind"
	byAgentFeatureRoleQuery, err := stub.GetStateByPartialCompositeKey(indexName, []string{"idfeature12"})

	// ==== Run the byAgentFeatureRole query ====
	// byAgentFeatureRoleQuery, err := a.GetByAgentFeatureRole(agentId, featureId, agentRole, stub)
	if err != nil {
		reputationInnMindInvokeCallLog.Info("Failed to get reputationInnMind for this agent: " + agentId + ", in this feature: " + featureId + ", in this role: " + agentRole)
		return shim.Error(err.Error())
	}

	// ==== Get the FeatureEvaluations for the byDemanderExecuter query result ====
	reputationInnMinds, err := a.GetInnMindReputationSliceFromRangeQuery(byAgentFeatureRoleQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== Marshal the byFeatureTxId query result ====
	featureEvaluationsAsJSON, err := json.Marshal(reputationInnMinds)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(featureEvaluationsAsJSON)
}

// TODO: Trovare il modo di generalizzare senza usare assets.Feature
// =====================================================================================================================
// Get history of a general asset in the Chain - The chain is a transaction log, structured as hash-linked blocks
// (https://hyperledger-fabric.readthedocs.io/en/release-1.1/ledger.html)
//
// Shows Off GetHistoryForKey() - reading complete history of a key/value
//
// Inputs - Array of strings
//  0
//  id
//  "m01490985296352SjAyM"
// =====================================================================================================================
func GetInnMindReputationHistory(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var reputationInnMindHistory []a.InnMindReputation

	var value a.InnMindReputation

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	key := args[0]
	fmt.Printf("- start GetHistory: %s\n", key)

	// Get History
	resultsIterator, err := stub.GetHistoryForKey(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	for resultsIterator.HasNext() {
		historyData, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		var singleInnMindReputation a.InnMindReputation
		json.Unmarshal(historyData.Value, &value) //un stringify it aka JSON.parse()
		if historyData.Value == nil {             //value has been deleted
		} else {
			json.Unmarshal(historyData.Value, &value) //un stringify it aka JSON.parse()
			singleInnMindReputation = value
		}
		reputationInnMindHistory = append(reputationInnMindHistory, singleInnMindReputation)
	}

	//change to array of bytes
	// historyAsBytes, _ := json.Marshal(history) //convert to array of bytes

	realAsBytes, _ := json.Marshal(reputationInnMindHistory)
	return shim.Success(realAsBytes)
}
