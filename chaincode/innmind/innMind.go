/*
Package main is the entry point of the hyperledger fabric chaincode and implements the shim.ChaincodeStubInterface
*/
/*
Created by Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com
*/
package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	a "github.com/pavva91/assets"
	gen "github.com/pavva91/generalcc"
	in "github.com/pavva91/invokeapi"
)

var log = shim.NewLogger("innMind")

const (
	InitLedger                                            = "InitLedger"
	CreateLeafFeature                                     = "CreateLeafFeature"
	CreateCompositeFeature                                = "CreateCompositeFeature"
	CreateFeature                                         = "CreateFeature"
	CreateAgent                                           = "CreateAgent"
	CreateFeatureRelationAgent                            = "CreateFeatureRelationAgent"
	CreateFeatureRelationAgentAndInnMindReputation        = "CreateFeatureRelationAgentAndInnMindReputation"
	CreateFeatureAndFeatureRelationAgentWithStandardValue = "CreateFeatureAndFeatureRelationAgentWithStandardValue"
	CreateFeatureAndFeatureRelationAgent                  = "CreateFeatureAndFeatureRelationAgent"
	GetFeatureHistory                                     = "GetFeatureHistory"
	GetFeature                                            = "GetFeature"
	GetAgent                                              = "GetAgent"
	GetFeatureRelationAgent                               = "GetFeatureRelationAgent"
	GetFeatureNotFoundError                               = "GetFeatureNotFoundError"
	GetAgentNotFoundError                                 = "GetAgentNotFoundError"
	ByFeature                                             = "byFeature"
	ByAgent                                               = "byAgent"
	GetAgentsByFeature                                    = "GetAgentsByFeature"
	GetFeaturesByAgent                                    = "GetFeaturesByAgent"
	GetFeaturesByName                                     = "GetFeaturesByName"
	DeleteFeature                                         = "DeleteFeature"
	DeleteAgent                                           = "DeleteAgent"
	DeleteFeatureRelationAgent                            = "DeleteFeatureRelationAgent"
	UpdateFeatureRelationAgentCost                        = "UpdateFeatureRelationAgentCost"
	UpdateFeatureRelationAgentTime                        = "UpdateFeatureRelationAgentTime"
	UpdateFeatureRelationAgentDescription                 = "UpdateFeatureRelationAgentDescription"
	CreateReview                                          = "CreateReview"
	GetReview                                             = "GetReview"
	ByReviewedFeatureTxId                                 = "byReviewedFeatureTxId"
	ByStartupExpert                                       = "byStartupExpert"
	GetReviewsByFeatureTxId                               = "GetReviewsByFeatureTxId"
	GetReviewsByStartupExpertTimestamp                    = "GetReviewsByStartupExpertTimestamp"
	CreateInnMindReputation                               = "CreateInnMindReputation"
	ModifyInnMindReputationValue                          = "ModifyInnMindReputationValue"
	ModifyOrCreateInnMindReputationValue                  = "ModifyOrCreateInnMindReputationValue"
	GetInnMindReputation                                  = "GetInnMindReputation"
	GetInnMindReputationNotFoundError                     = "GetInnMindReputationNotFoundError"
	ByAgentFeatureRole                                    = "byAgentFeatureRole"
	GetInnMindReputationsByAgentFeatureRole               = "GetInnMindReputationsByAgentFeatureRole"
	Write                                                 = "Write"
	Read                                                  = "Read"
	ReadEverything                                        = "ReadEverything"
	GetHistory                                            = "GetHistory"
	GetInnMindReputationHistory                           = "GetInnMindReputationHistory"
	AllStateDB                                            = "AllStateDB"
	GetValue                                              = "GetValue"
	HelloWorld                                            = "HelloWorld"
)

// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
	testMode bool
}

// =====================================================================================================================
// Main
// =====================================================================================================================
func main() {
	simpleChaincode := new(SimpleChaincode)
	simpleChaincode.testMode = false
	err := shim.Start(simpleChaincode)
	if err != nil {
		fmt.Printf("Error starting Simple chaincode - %s", err)
	}
}

// =====================================================================================================================
// Init - initialize the chaincode
// =====================================================================================================================
// The Init method is called when the Smart Contract "trustreputationledger" is instantiated by the blockchain network
// Best practice is to have any Ledger initialization in separate function -- see InitLedger()
// =====================================================================================================================
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	// TEST BEHAVIOUR
	if t.testMode {
		a.InitInnMindLedger(stub)
	}
	// NORMAL BEHAVIOUR
	return shim.Success(nil)
}

// =====================================================================================================================
// Invoke - Our entry point for Invocations
// =====================================================================================================================
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	log.Info("########### INVOKE: " + function + " ###########")

	// Route to the appropriate handler function to interact with the ledger appropriately
	switch function {
	// AGENT, SERVICE, AGENT SERVICE RELATION INVOKES

	// CREATE:
	case InitLedger:
		response := a.InitLedger(stub)
		return response
	case CreateLeafFeature:
		return in.CreateLeafFeature(stub, args)
	case CreateCompositeFeature:
		return in.CreateCompositeFeature(stub, args)
	case CreateFeature:
		return in.CreateFeature(stub, args)
	case CreateAgent:
		return in.CreateAgent(stub, args)
	case CreateFeatureRelationAgent:
		// Already with reference integrity controls (feature already exist, agent already exist, relation don't already exist)
		// NO REPUTATION INITIALIZATION
		return in.CreateFeatureRelationAgent(stub, args)

		// GET:
	case GetFeatureHistory:
		return a.GetFeatureHistory(stub, args)
	case GetFeature:
		return in.QueryFeature(stub, args)
	case GetAgent:
		return in.QueryAgent(stub, args)
	case GetFeatureRelationAgent:
		return in.QueryFeatureRelationAgent(stub, args)

		// GET NOT FOUND (DEPRECATED):
	case GetFeatureNotFoundError:
		return in.QueryFeatureNotFoundError(stub, args)
	case GetAgentNotFoundError:
		return in.QueryAgentNotFoundError(stub, args)

		// RANGE QUERY:
	case ByFeature:
		return in.QueryByFeatureRelationAgent(stub, args)
	case ByAgent:
		return in.QueryByAgentFeatureRelation(stub, args)
	case GetAgentsByFeature:
		// also with only one record result return always a JSONArray
		return in.GetFeatureRelationAgentByFeatureWithCostAndTime(stub, args)
	case GetFeaturesByAgent:
		// also with only one record result return always a JSONArray
		return in.GetFeatureRelationAgentByAgentWithCostAndTimeNotFoundError(stub, args)
	case GetFeaturesByName:
		return in.QueryByFeatureName(stub, args)

		// DELETE:
	case DeleteFeature:
		return a.DeleteFeature(stub, args)
	case DeleteAgent:
		return a.DeleteAgent(stub, args)
	case DeleteFeatureRelationAgent:
		return in.DeleteFeatureRelationAgentAndIndexes(stub, args)

		// UPDATE:
	case UpdateFeatureRelationAgentCost:
		return in.UpdateFeatureRelationAgentCost(stub, args)
	case UpdateFeatureRelationAgentTime:
		return in.UpdateFeatureRelationAgentTime(stub, args)
	case UpdateFeatureRelationAgentDescription:
		return in.UpdateFeatureRelationAgentDescription(stub, args)

	// ACTIVITY INVOKES
	// CREATE:
	case CreateReview:
		return in.CreateReview(stub, args)
		// GET:
	case GetReview:
		return in.QueryReview(stub, args)
		// RANGE QUERY:
	case ByReviewedFeatureTxId:
		return in.QueryByReviewedFeatureTx(stub, args)
	case ByStartupExpert:
		return in.QueryByStartupExpert(stub, args)
	case GetReviewsByFeatureTxId:
		// also with only one record result return always a JSONArray
		return in.GetReviewsByReviewedFeatureTxId(stub, args)
	case GetReviewsByStartupExpertTimestamp:
		// also with only one record result return always a JSONArray
		return in.GetReviewsByStartupExpertTimestamp(stub, args)

	// REPUTATION INVOKES
	// CREATE:
	case CreateInnMindReputation:
		return in.CreateInnMindReputation(stub, args)
		// MODIFTY:
	case ModifyInnMindReputationValue:
		return in.ModifyInnMindReputationValue(stub, args)
	case ModifyOrCreateInnMindReputationValue:
		return in.ModifyOrCreateInnMindReputationValue(stub, args)

		// GET:
	case GetInnMindReputation:
		return in.QueryInnMindReputation(stub, args)
	case GetInnMindReputationNotFoundError:
		return in.QueryInnMindReputationNotFoundError(stub, args)
		// RANGE QUERY:
	case ByAgentFeatureRole:
		return in.QueryByAgentFeatureRole(stub, args)
	case GetInnMindReputationsByAgentFeatureRole:
		// also with only one record result return always a JSONArray
		return in.GetInnMindReputationsByAgentFeatureRole(stub, args)

		// GENERAL INVOKES
	case Write:
		return gen.Write(stub, args)
	case Read:
		return gen.Read(stub, args)
	case ReadEverything:
		return a.ReadEverything(stub)
	case GetHistory:
		// Get Block Chain Transaction Log of that assetId
		return gen.GetHistory(stub, args)
	case GetInnMindReputationHistory:
		return in.GetInnMindReputationHistory(stub, args)
	case AllStateDB:
		// All Records Level DB (World State DB)
		return gen.ReadAllStateDB(stub)
	case GetValue:
		return gen.GetValue(stub, args)

	default: // should be unreachable
		log.Error("Invalid Smart Contract function Name: " + function)
		return shim.Error("Invalid Smart Contract function Name: " + function)
	}

}

// =====================================================================================================================
// Query - legacy function
// =====================================================================================================================
func (t *SimpleChaincode) Query(stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Error("Unknown supported call - Query()")
}
