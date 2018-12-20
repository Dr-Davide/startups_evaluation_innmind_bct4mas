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
	a "github.com/pavva91/assets"
)

var featureRelationAgentInvokeCallLog = shim.NewLogger("featureRelationAgentInvokeCall")

// ========================================================================================================================
// Init Feature Relation Agent  - wrapper of CreateFeatureRelationAgentAndReputation called from chiancode's Invoke
// ========================================================================================================================
func CreateFeatureRelationAgent(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0               1       2       3       4
	// "FeatureId", "AgentId", "Cost", "Time", "Description"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 5)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	// ==== Set Variables from Passed Arguments ====
	featureId := args[0]
	agentId := args[1]
	cost := args[2]
	time := args[3]
	description := args[4]

	// ==== Check if already existing feature ====
	feature, errS := a.GetFeatureNotFoundError(stub, featureId)
	if errS != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find feature by id " + featureId)
		return shim.Error("Failed to find feature by id " + errS.Error())
	}
	featureRelationAgentInvokeCallLog.Info("Feature Already existing ok")

	// ==== Check if already existing agent ====
	agent, errA := a.GetAgentNotFoundError(stub, agentId)
	if errA != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find agent by id " + agentId)
		return shim.Error("Failed to find agent by id: " + errA.Error())
	}
	featureRelationAgentInvokeCallLog.Info("Agent Already existing ok")

	// ==== Check, Create, Indexing FeatureRelationAgent ====

	featureRelationAgent, featureRelationError := a.CheckingCreatingIndexingFeatureRelationAgent(featureId, agentId, cost, time, description, stub)
	if featureRelationError != nil {
		return shim.Error("Error saving FeatureRelationAgent: " + featureRelationError.Error())

	}

	// ==== FeatureRelationAgent saved and indexed. Set Event ====

	eventPayload := "Created Feature RelationAgent: " + featureId + " with agent: " + agentId
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureRelationAgentCreatedEvent", payloadAsBytes)
	if eventError != nil {
		featureRelationAgentInvokeCallLog.Info("Error in event Creation: " + eventError.Error())
	} else {
		featureRelationAgentInvokeCallLog.Info("Event Create FeatureRelationAgent OK")
	}
	// ==== FeatureRelationAgent saved & indexed. Return success ====
	featureRelationAgentInvokeCallLog.Info("Feature: " + feature.Name + " mapped with agent: " + agent.Name + " with cost: " + featureRelationAgent.Cost + " and time: " + featureRelationAgent.Time)
	return shim.Success(nil)
}

// ========================================================================================================================
// Init Feature Agent Relation - wrapper of CreateFeatureRelationAgentAndReputation called from chiancode's Invoke
// ========================================================================================================================
func CreateFeatureRelationAgentAndReputation(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0               1       2       3        4
	// "FeatureId", "AgentId", "Cost", "Time", "Description"
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

	// ==== Set Variables from Passed Arguments ====
	featureId := args[0]
	agentId := args[1]
	cost := args[2]
	time := args[3]
	description := args[4]

	// ==== Check if already existing feature ====
	feature, errS := a.GetFeatureNotFoundError(stub, featureId)
	if errS != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find feature by id " + featureId)
		return shim.Error("Failed to find feature by id " + errS.Error())
	}
	featureRelationAgentInvokeCallLog.Info("Feature Already existing ok")

	// ==== Check if already existing agent ====
	agent, errA := a.GetAgentNotFoundError(stub, agentId)
	if errA != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find agent by id " + agentId)
		return shim.Error("Failed to find agent by id: " + errA.Error())
	}
	featureRelationAgentInvokeCallLog.Info("Agent Already existing ok")

	// ==== Check, Create, Indexing FeatureRelationAgent ====

	featureRelationAgent, featureRelationError := a.CheckingCreatingIndexingFeatureRelationAgent(featureId, agentId, cost, time, description, stub)
	if featureRelationError != nil {
		return shim.Error("Error saving FeatureRelationAgent: " + featureRelationError.Error())

	}

	// ==== Check, Create, Indexing Reputation ====
	initReputationValue := "6"
	reputation, reputationError := a.CheckingCreatingIndexingReputation(agentId, featureId, a.Executer, initReputationValue, stub)
	if reputationError != nil {
		return shim.Error("Error saving Agent reputation: " + reputationError.Error())
	}

	// ==== FeatureRelationAgent saved and indexed. Set Event ====

	eventPayload := "Created Feature RelationAgent: " + featureId + " with agent: " + agentId
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureRelationAgentAndReputationCreatedEvent", payloadAsBytes)
	if eventError != nil {
		featureRelationAgentInvokeCallLog.Info("Error in event Creation: " + eventError.Error())
	} else {
		featureRelationAgentInvokeCallLog.Info("Event Create FeatureRelationAgent OK")
	}
	// ==== FeatureRelationAgent saved & indexed. Return success ====
	featureRelationAgentInvokeCallLog.Info("Feature: " + feature.Name + " mapped with agent: " + agent.Name + " with cost: " + featureRelationAgent.Cost + " and time: " + featureRelationAgent.Time + " nella relazione con reputazione iniziale: " + reputation.Value)
	return shim.Success(nil)
}

// ========================================================================================================================
// Modify Feature Relation Agent Cost - wrapper of UpdateFeatureRelationAgentCost called from chiancode's Invoke
// ========================================================================================================================
func UpdateFeatureRelationAgentCost(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0            1
	// "relationId", "newRelationCost"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 2)
	if argumentSizeError != nil {
		featureRelationAgentInvokeCallLog.Error(argumentSizeError.Error())
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		featureRelationAgentInvokeCallLog.Error(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	relationId := args[0]
	newRelationCost := args[1]

	// ==== get the featureRelationAgent ====
	featureRelationAgent, getError := a.GetFeatureRelationAgentNotFoundError(stub, relationId)
	if getError != nil {
		featureRelationAgentInvokeCallLog.Info(getError.Error())
		return shim.Error(getError.Error())
	}

	// ==== modify the featureRelationAgent ====
	modifyError := a.UpdateFeatureRelationAgentCost(featureRelationAgent, newRelationCost, stub)
	if modifyError != nil {
		featureRelationAgentInvokeCallLog.Error(modifyError.Error())
		return shim.Error(modifyError.Error())
	}

	// ==== FeatureRelationAgent Cost modified. Set Event ====

	eventPayload := "Modified Feature RelationAgent: " + featureRelationAgent.FeatureId + " with agent: " + featureRelationAgent.AgentId + "from old cost value: " + featureRelationAgent.Cost + "to new cost value: " + newRelationCost
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureRelationAgentCostUpdatedEvent", payloadAsBytes)
	if eventError != nil {
		featureRelationAgentInvokeCallLog.Info("Error in event Creation: " + eventError.Error())
	} else {
		featureRelationAgentInvokeCallLog.Info("Event Modifiy FeatureRelationAgent Cost OK")
	}

	featureRelationAgentInvokeCallLog.Info("Modify Feature RelationAgent Time OK")
	return shim.Success(nil)
}

// ========================================================================================================================
// Modify Feature Relation Agent Time - wrapper of UpdateFeatureRelationAgentTime called from chiancode's Invoke
// ========================================================================================================================
func UpdateFeatureRelationAgentTime(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0            1
	// "relationId", "newRelationTime"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 2)
	if argumentSizeError != nil {
		featureRelationAgentInvokeCallLog.Error(argumentSizeError.Error())
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		featureRelationAgentInvokeCallLog.Error(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	relationId := args[0]
	newRelationTime := args[1]

	// ==== get the featureRelationAgent ====
	featureRelationAgent, getError := a.GetFeatureRelationAgentNotFoundError(stub, relationId)
	if getError != nil {
		featureRelationAgentInvokeCallLog.Error(getError.Error())
		return shim.Error(getError.Error())
	}

	// ==== modify the featureRelationAgent ====
	modifyError := a.UpdateFeatureRelationAgentTime(featureRelationAgent, newRelationTime, stub)
	if modifyError != nil {
		featureRelationAgentInvokeCallLog.Info(modifyError.Error())
		return shim.Error(modifyError.Error())
	}

	// ==== FeatureRelationAgent Time modified. Set Event ====

	eventPayload := "Modified Feature RelationAgent: " + featureRelationAgent.FeatureId + " with agent: " + featureRelationAgent.AgentId + "from old time value: " + featureRelationAgent.Time + "to new time value: " + newRelationTime
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureRelationAgentTimeUpdatedEvent", payloadAsBytes)
	if eventError != nil {
		featureRelationAgentInvokeCallLog.Info("Error in event Creation: " + eventError.Error())
	} else {
		featureRelationAgentInvokeCallLog.Info("Event Modifiy FeatureRelationAgent Time OK")
	}

	featureRelationAgentInvokeCallLog.Info("Modify Feature RelationAgent Time OK")
	return shim.Success(nil)
}

// ========================================================================================================================
// Update Feature Relation Agent Description - wrapper of UpdateFeatureRelationAgentDescription called from chiancode's Invoke
// ========================================================================================================================
func UpdateFeatureRelationAgentDescription(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0            1
	// "relationId", "newRelationDescription"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 2)
	if argumentSizeError != nil {
		featureRelationAgentInvokeCallLog.Error(argumentSizeError.Error())
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		featureRelationAgentInvokeCallLog.Error(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	relationId := args[0]
	newRelationDescription := args[1]

	// ==== get the featureRelationAgent ====
	featureRelationAgent, getError := a.GetFeatureRelationAgentNotFoundError(stub, relationId)
	if getError != nil {
		featureRelationAgentInvokeCallLog.Error(getError.Error())
		return shim.Error(getError.Error())
	}

	// ==== modify the featureRelationAgent ====
	modifyError := a.UpdateFeatureRelationAgentDescription(featureRelationAgent, newRelationDescription, stub)
	if modifyError != nil {
		featureRelationAgentInvokeCallLog.Info(modifyError.Error())
		return shim.Error(modifyError.Error())
	}

	// ==== FeatureRelationAgent Time modified. Set Event ====

	eventPayload := "Modified Feature RelationAgent: " + featureRelationAgent.FeatureId + " with agent: " + featureRelationAgent.AgentId + "from old time value: " + featureRelationAgent.Time + "to new time value: " + newRelationDescription
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureRelationAgentDescriptionUpdatedEvent", payloadAsBytes)
	if eventError != nil {
		featureRelationAgentInvokeCallLog.Info("Error in event Creation: " + eventError.Error())
	} else {
		featureRelationAgentInvokeCallLog.Info("Event Modifiy FeatureRelationAgent Time OK")
	}

	featureRelationAgentInvokeCallLog.Info("Modify Feature RelationAgent Time OK")
	return shim.Success(nil)
}

// ============================================================================================================================
// Query FeatureRelationAgent - wrapper of GetFeatureNotFoundError called from the chaincode invoke
// ============================================================================================================================
func QueryFeatureRelationAgent(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "relationId"
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

	relationId := args[0]

	// ==== get the featureRelationAgent ====
	featureRelationAgent, err := a.GetFeatureRelationAgent(stub, relationId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find featureRelationAgent by id " + relationId)
		return shim.Error(err.Error())
	} else {
		featureRelationAgentInvokeCallLog.Info("Feature ID: " + featureRelationAgent.FeatureId + ", Agent: " + featureRelationAgent.AgentId + ", with Cost: " + featureRelationAgent.Cost + ", with Time: " + featureRelationAgent.Time)
		// ==== Marshal the byFeature query result ====
		featureAsJSON, err := json.Marshal(featureRelationAgent)
		if err != nil {
			return shim.Error(err.Error())
		}
		return shim.Success(featureAsJSON)
	}
}

// ========================================================================================================================
// Query by Feature Agent Relation - wrapper of GetByFeature called from chiancode's Invoke
// ========================================================================================================================
func QueryByFeatureRelationAgent(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "FeatureId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	featureRelationAgentInvokeCallLog.Info("- start init featureRelationAgent")

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	featureId := args[0]

	// ==== Check if already existing feature ====
	feature, err := a.GetFeatureNotFoundError(stub, featureId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find feature  by id " + featureId)
		return shim.Error(err.Error())
	}

	// ==== Run the byFeature query ====
	byFeatureQuery, err := a.GetFeatureRelationAgentByFeature(featureId, stub)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to get feature relation " + featureId)
		return shim.Error(err.Error())
	}

	featureRelationAgentInvokeCallLog.Info("Agents that expose the feature: %s", feature.Name)

	// ==== Print the byFeature query result ====
	err = a.PrintByFeatureResultsIterator(byFeatureQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== AgentFeatureRelation saved & indexed. Return success with payload====
	return shim.Success(nil)
}

// ========================================================================================================================
// GetAgentsByFeature - wrapper of GetByFeature called from chiancode's Invoke, for looking for agents that provide certain feature
// ========================================================================================================================
func GetAgentsByFeature(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "FeatureId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	featureRelationAgentInvokeCallLog.Info("- start init featureRelationAgent")

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	featureId := args[0]

	// ==== Check if already existing feature ====
	feature, err := a.GetFeatureNotFoundError(stub, featureId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The feature doesn't exist " + featureId)
		return shim.Error(err.Error())
	}

	// ==== Run the byFeature query ====
	byFeatureQuery, err := a.GetFeatureRelationAgentByFeature(featureId, stub)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The feature " + feature.Name + " is not mapped with any agent " + featureId)
		return shim.Error(err.Error())
	}

	// ==== Get the Agents for the byFeature query result ====
	agentSlice, err := a.GetAgentSliceFromByFeatureQuery(byFeatureQuery, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== Marshal the byFeature query result ====
	agentsAsJSON, err := json.Marshal(agentSlice)
	if err != nil {
		return shim.Error(err.Error())
	}

	// ==== AgentFeatureRelation saved & indexed. Return success with payload====
	return shim.Success(agentsAsJSON)
}

// ========================================================================================================================
// GetFeatureRelationAgentByFeatureWithCostAndTime - wrapper of GetByFeature called from chiancode's Invoke, for looking for agents that provide certain feature
// ========================================================================================================================
func GetFeatureRelationAgentByFeatureWithCostAndTime(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "FeatureId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	featureRelationAgentInvokeCallLog.Info("- start init featureRelationAgent")

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	featureId := args[0]

	// ==== Check if already existing feature ====
	feature, err := a.GetFeatureNotFoundError(stub, featureId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The feature doesn't exist " + featureId)
		return shim.Error("The feature doesn't exist: " + err.Error())
	}

	// ==== Run the byFeature query ====
	byFeatureQueryIterator, err := a.GetFeatureRelationAgentByFeature(featureId, stub)
	// byFeatureQueryIterator, err := stub.GetStateByPartialCompositeKey("feature~agent~relation", []string{featureId})
	defer byFeatureQueryIterator.Close()

	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The feature " + feature.Name + " is not mapped with any agent " + featureId)
		return shim.Error(err.Error())
	}
	if byFeatureQueryIterator != nil {
		featureRelationAgentInvokeCallLog.Info(&byFeatureQueryIterator)
	}

	// ==== Get the Agents for the byFeature query result ====
	featureRelationSlice, err := a.GetFeatureRelationSliceFromRangeQuery(byFeatureQueryIterator, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	byFeatureQueryIterator.Close()
	// ==== Marshal the byFeature query result ====
	fmt.Print(featureRelationSlice)
	agentsByFeatureAsBytes, err := json.Marshal(featureRelationSlice)
	if err != nil {
		return shim.Error(err.Error())
	}
	featureRelationAgentInvokeCallLog.Info(agentsByFeatureAsBytes)

	stringOut := string(agentsByFeatureAsBytes)
	featureRelationAgentInvokeCallLog.Info(stringOut)

	// TODO: Let the null go in the payload
	// null = empty query result
	//if stringOut == "null" {
	//	featureRelationAgentInvokeCallLog.Info("Feature exists but has no existing relationships with agents")
	//	return shim.Error("Feature exists but has no existing relationships with agents")
	//}

	// ==== Return success with agentsByFeatureSliceAsBytes as payload ====
	return shim.Success(agentsByFeatureAsBytes)
}

// ========================================================================================================================
// GetFeatureRelationAgentByFeatureWithCostAndTimeNotFoundError - wrapper of GetByFeature called from chiancode's Invoke, for looking for agents that provide certain feature, return Error if not found
// ========================================================================================================================
func GetFeatureRelationAgentByAgentWithCostAndTimeNotFoundError(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "AgentId"

	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	featureRelationAgentInvokeCallLog.Info("- start init featureRelationAgent")

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	agentId := args[0]

	// ==== Check if already existing agent ====
	agent, err := a.GetAgentNotFoundError(stub, agentId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The agent doesn't exist " + agentId)
		return shim.Error("The agent doesn't exist: " + err.Error())
	}

	// ==== Run the byAgent query ====
	byAgentQueryIterator, err := a.GetFeatureRelationAgentByAgent(agentId, stub)
	defer byAgentQueryIterator.Close()

	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The agent " + agent.Name + " is not mapped with any feature " + agentId)
		return shim.Error(err.Error())
	}
	if byAgentQueryIterator != nil {
		featureRelationAgentInvokeCallLog.Info(&byAgentQueryIterator)
	}

	// ==== Get the Agents for the byFeature query result ====
	featureRelationSlice, err := a.GetFeatureRelationSliceFromRangeQuery(byAgentQueryIterator, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	byAgentQueryIterator.Close()
	// ==== Marshal the byFeature query result ====
	fmt.Print(featureRelationSlice)
	featuresByAgentAsBytes, err := json.Marshal(featureRelationSlice)
	if err != nil {
		return shim.Error(err.Error())
	}
	featureRelationAgentInvokeCallLog.Info(featuresByAgentAsBytes)

	stringOut := string(featuresByAgentAsBytes)
	featureRelationAgentInvokeCallLog.Info(stringOut)
	if stringOut == "null" {
		featureRelationAgentInvokeCallLog.Info("Feature exists but has no existing relationships with this agent")
		return shim.Error("Feature exists but has no existing relationships with this agent")
	}

	// ==== AgentFeatureRelation saved & indexed. Return success with payload ====
	return shim.Success(featuresByAgentAsBytes)
}

// ========================================================================================================================
// GetFeatureRelationAgentByFeatureWithCostAndTime - wrapper of GetByAgent called from chiancode's Invoke, for looking for features provided by the agent, return null if not found
// ========================================================================================================================
func GetFeatureRelationAgentByAgentWithCostAndTime(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "AgentId"

	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	featureRelationAgentInvokeCallLog.Info("- start init featureRelationAgent")

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	agentId := args[0]

	// ==== Check if already existing agent ====
	agent, err := a.GetAgentNotFoundError(stub, agentId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The agent doesn't exist " + agentId)
		return shim.Error("The agent doesn't exist: " + err.Error())
	}

	// ==== Run the byAgent query ====
	byAgentQueryIterator, err := a.GetByAgent(agentId, stub)
	defer byAgentQueryIterator.Close()

	if err != nil {
		featureRelationAgentInvokeCallLog.Info("The agent " + agent.Name + " is not mapped with any feature " + agentId)
		return shim.Error(err.Error())
	}
	if byAgentQueryIterator != nil {
		featureRelationAgentInvokeCallLog.Info(&byAgentQueryIterator)
	}

	// ==== Get the Agents for the byFeature query result ====
	featureRelationSlice, err := a.GetFeatureRelationSliceFromRangeQuery(byAgentQueryIterator, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	byAgentQueryIterator.Close()
	// ==== Marshal the byFeature query result ====
	fmt.Print(featureRelationSlice)
	featuresByAgentAsBytes, err := json.Marshal(featureRelationSlice)
	if err != nil {
		return shim.Error(err.Error())
	}
	featureRelationAgentInvokeCallLog.Info(featuresByAgentAsBytes)

	stringOut := string(featuresByAgentAsBytes)
	featureRelationAgentInvokeCallLog.Info("stringOut Value: " + stringOut)

	// ==== AgentFeatureRelation saved & indexed. Return success with payload ====
	return shim.Success(featuresByAgentAsBytes)
}

// ========================================================================================================================
// Query by Agent Feature Relation - wrapper of GetByAgent called from chiancode's Invoke
// ========================================================================================================================
func QueryByAgentFeatureRelation(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "AgentId"
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

	agentId := args[0]

	// ==== Check if already existing agent ====
	agent, err := a.GetAgentNotFoundError(stub, agentId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find agent  by id " + agentId)
		return shim.Error(err.Error())
	}

	// ==== Run the byAgent query ====
	byAgentQuery, err := a.GetByAgent(agentId, stub)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to get agent relation " + agentId)
		return shim.Error(err.Error())
	}

	fmt.Printf("The agent %s expose the features:\n", agent.Name)

	// ==== Print the byFeature query result ====
	printError := a.PrintByAgentResultsIterator(byAgentQuery, stub)
	if printError != nil {
		return shim.Error(printError.Error())
	}

	// ==== AgentFeatureRelation saved & indexed. Return success ====
	return shim.Success(nil)
}

// ============================================================================================================================
// Remove Feature Agent Relation - wrapper of DeleteFeatureRelationAgent a marble from state and from marble index Shows Off DelState() - "removing"" a key/value from the ledger
// UNSAFE function
// ============================================================================================================================
func DeleteFeatureRelationAgent(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	featureRelationAgentInvokeCallLog.Info("starting delete featureRelationAgent agent relation")

	//   0
	// "RelationId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// input sanitation
	err := arglib.SanitizeArguments(args)
	if err != nil {
		return shim.Error(err.Error())
	}

	relationId := args[0]

	// get the featureRelationAgent
	featureRelationAgent, err := a.GetFeatureRelationAgent(stub, relationId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find featureRelationAgent by relationId " + relationId)
		return shim.Error(err.Error())
	}

	// remove the featureRelationAgent
	err = a.DeleteFeatureRelationAgent(stub, relationId) //remove the key from chaincode state
	if err != nil {
		return shim.Error("Failed to delete state")
	}

	fmt.Printf("Deleted featureRelationAgent that maps FeatureId: %s, with AgentId: %s of Cost: %s, Time: %s\n", featureRelationAgent.FeatureId, featureRelationAgent.AgentId, featureRelationAgent.Cost, featureRelationAgent.Time)
	return shim.Success(nil)
}

// =====================================================================================================================
// DeleteFeatureRelationAgentApi() - remove a feature from state and from feature index
//
// Shows Off DelState() - "removing"" a key/value from the ledger
//
// Inputs:
//      0
//     RelationId
// =====================================================================================================================
func DeleteFeatureRelationAgentAndIndexes(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	//   0
	// "RelationId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		featureRelationAgentInvokeCallLog.Error(argumentSizeError)
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}
	// input sanitation
	err := arglib.SanitizeArguments(args)
	if err != nil {
		featureRelationAgentInvokeCallLog.Error(err.Error())
		return shim.Error(err.Error())
	}

	// get args into variables
	relationId := args[0]

	// get the featureRelationAgent
	featureRelationAgent, err := a.GetFeatureRelationAgentNotFoundError(stub, relationId)
	if err != nil {
		featureRelationAgentInvokeCallLog.Info("Failed to find featureRelationAgent by relationId " + relationId)
		return shim.Error(err.Error())
	}

	// ==== remove the featureRelationAgent ====
	err = stub.DelState(relationId) //remove the key from chaincode state
	if err != nil {
		return shim.Error("Failed to delete featureRelationAgent: " + err.Error())
	}

	// ==== remove the indexes ====
	indexNameFeature := "feature~agent~relation"
	err = a.DeleteFeatureIndexFeatureRelationAgent(stub, indexNameFeature, featureRelationAgent.FeatureId, featureRelationAgent.AgentId, featureRelationAgent.RelationId)
	if err != nil {
		return shim.Error("Failed to delete featureRelationAgent Agent Index: " + err.Error())
	}

	indexNameAgent := "agent~feature~relation"
	err = a.DeleteAgentIndex(stub, indexNameAgent, featureRelationAgent.AgentId, featureRelationAgent.FeatureId, featureRelationAgent.RelationId)
	if err != nil {
		return shim.Error("Failed to delete featureRelationAgent Agent Index: " + err.Error())
	}

	// ==== FeatureRelationAgent and indexed deleted. Set Event ====
	eventPayload := "Deleted Feature RelationAgent: " + featureRelationAgent.RelationId + ", of feature: " + featureRelationAgent.FeatureId + ", with agent: " + featureRelationAgent.AgentId
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureRelationAgentDeletedEvent", payloadAsBytes)
	if eventError != nil {
		featureRelationAgentInvokeCallLog.Error("Error in event Creation: " + eventError.Error())
	} else {
		featureRelationAgentInvokeCallLog.Info("Event Delete FeatureRelationAgent OK")
	}

	// ==== FeatureRelationAgent saved & indexed. Return success ====
	featureRelationAgentInvokeCallLog.Info("Deleted featureRelationAgent: " + featureRelationAgent.RelationId)
	return shim.Success(nil)
}
