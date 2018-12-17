/*
Package invokeapi is the middle layer between the Chaincode entry point (main package) and the Assets (assets package)
that is called directly from the chaincode's Invoke funtions and aggregate the calls to the assets to follow the
"business logic"
*/
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
	"strconv"

	// a "github.com/pavva91/trustreputationledger/assets"
	a "github.com/pavva91/assets"
)

var featureInvokeCallLog = shim.NewLogger("featureInvokeCall")

// =====================================================================================================================
// Create Leaf Feature - wrapper of CreateLeafFeature called from the chaincode invoke
// =====================================================================================================================
func CreateLeafFeature(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0               1
	// "FeatureId", "featureName"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 2)
	if argumentSizeError != nil {
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}
	featureInvokeCallLog.Info("- start create Leaf feature")

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	featureId := args[0]
	featureName := args[1]

	// ==== Check if feature already exists ====
	featureAsBytes, err := a.GetFeatureAsBytes(stub, featureId)
	if err != nil {
		return shim.Error("Failed to get feature: " + err.Error())
	} else if featureAsBytes != nil {
		featureInvokeCallLog.Info("This feature already exists: " + featureId)
		return shim.Error("This feature already exists: " + featureId)
	}

	feature, err := a.CreateLeafFeature(featureId, featureName, stub)
	if err != nil {
		return shim.Error("Failed to create the feature: " + err.Error())
	}

	// ==== Indexing of feature by Name (to do query by Name, if you want) ====
	// index create
	nameIndexKey, nameIndexError := a.CreateNameIndexFeature(feature, stub)
	if nameIndexError != nil {
		return shim.Error(nameIndexError.Error())
	}
	featureInvokeCallLog.Info(nameIndexKey)

	// index save
	saveIndexError := a.SaveIndex(nameIndexKey, stub)
	if saveIndexError != nil {
		return shim.Error(saveIndexError.Error())
	}

	// ==== Feature saved and indexed. Set Event ====

	eventPayload := "Created Feature: " + featureId
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureCreatedEvent", payloadAsBytes)
	if eventError != nil {
		featureInvokeCallLog.Error("Error in event Creation: " + eventError.Error())
	} else {
		featureInvokeCallLog.Info("Event Create Feature OK")
	}
	// ==== Feature saved and indexed. Return success ====
	featureInvokeCallLog.Info("FeatureId: " + feature.FeatureId + ", Name: " + feature.Name + " Succesfully Created - End Create Feature")
	return shim.Success(nil)
}

// =====================================================================================================================
// Create Composite Feature - wrapper of CreateLeafFeature called from the chaincode invoke
// =====================================================================================================================
func CreateCompositeFeature(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0               1             3
	// "FeatureId", "featureName",  "featureComposition"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 3)
	if argumentSizeError != nil {
		featureInvokeCallLog.Error(argumentSizeError.Error())
		return shim.Error("Argument Size Error: " + argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		featureInvokeCallLog.Error(sanitizeError)
		return shim.Error("Sanitize error: " + sanitizeError.Error())
	}

	featureId := args[0]
	featureName := args[1]
	featureCompositionAsString := args[2]

	featureComposition := arglib.ParseStringToStringSlice(featureCompositionAsString)

	featureInvokeCallLog.Info("- Start Create Composite feature: " + featureId)

	// ==== Check if feature already exists ====
	featureAsBytes, err := a.GetFeatureAsBytes(stub, featureId)
	if err != nil {
		featureInvokeCallLog.Error(err.Error())
		return shim.Error("Failed to get feature: " + err.Error())
	} else if featureAsBytes != nil {
		featureInvokeCallLog.Error("This feature already exists: " + featureId)
		return shim.Error("This feature already exists: " + featureId)
	}

	feature, err := a.CreateCompositeFeature(featureId, featureName, featureComposition, stub)
	if err != nil {
		featureInvokeCallLog.Error(err.Error())
		return shim.Error("Failed to create the feature: " + err.Error())
	}

	// ==== Indexing of feature by Name (to do query by Name, if you want) ====
	// index create
	nameIndexKey, nameIndexError := a.CreateNameIndexFeature(feature, stub)
	if nameIndexError != nil {
		featureInvokeCallLog.Error(nameIndexError.Error())
		return shim.Error(nameIndexError.Error())
	}
	featureInvokeCallLog.Info("nameIndexKey: " + nameIndexKey)

	// index save
	saveIndexError := a.SaveIndex(nameIndexKey, stub)
	if saveIndexError != nil {
		featureInvokeCallLog.Error(saveIndexError.Error())
		return shim.Error(saveIndexError.Error())
	}

	// ==== Feature saved and indexed. Set Event ====

	eventPayload := "Created Feature: " + featureId
	payloadAsBytes := []byte(eventPayload)
	eventError := stub.SetEvent("FeatureCreatedEvent", payloadAsBytes)
	if eventError != nil {
		featureInvokeCallLog.Error("Error in event Creation: " + eventError.Error())
	} else {
		featureInvokeCallLog.Info("Event Create Feature OK")
	}
	// ==== Feature saved and indexed. Return success ====
	featureInvokeCallLog.Info("FeatureId: " + feature.FeatureId + ", Name: " + feature.Name + " Succesfully Created - End Create Feature")
	return shim.Success(nil)
}

// =====================================================================================================================
// Create Feature - wrapper of CreateLeafFeature called from the chaincode invoke
// =====================================================================================================================
func CreateFeature(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0               1            3
	// "FeatureId", "featureName",  "featureComposition"
	fourArgumentSizeError := arglib.ArgumentSizeVerification(args, 3)
	if fourArgumentSizeError != nil {
		return CreateLeafFeature(stub, args)
	} else {

		featureId := args[0]
		featureName := args[1]
		featureCompositionAsString := args[2]

		featureComposition := arglib.ParseStringToStringSlice(featureCompositionAsString)

		// ==== Check if feature already exists ====
		featureAsBytes, err := a.GetFeatureAsBytes(stub, featureId)
		if err != nil {
			return shim.Error("Failed to get feature: " + err.Error())
		} else if featureAsBytes != nil {
			featureInvokeCallLog.Info("This feature already exists: " + featureId)
			return shim.Error("This feature already exists: " + featureId)
		}

		feature, err := a.CreateFeature(featureId, featureName, featureComposition, stub)
		if err != nil {
			return shim.Error("Failed to create the feature: " + err.Error())
		}

		// ==== Indexing of feature by Name (to do query by Name, if you want) ====
		// index create
		nameIndexKey, nameIndexError := a.CreateNameIndexFeature(feature, stub)
		if nameIndexError != nil {
			return shim.Error(nameIndexError.Error())
		}
		featureInvokeCallLog.Info("nameIndexKey: " + nameIndexKey)

		// index save
		saveIndexError := a.SaveIndex(nameIndexKey, stub)
		if saveIndexError != nil {
			return shim.Error(saveIndexError.Error())
		}

		// ==== Feature saved and indexed. Set Event ====
		transientMap, err := stub.GetTransient()
		transientData, ok := transientMap["event"]
		featureInvokeCallLog.Info("OK: " + strconv.FormatBool(ok))
		featureInvokeCallLog.Info(transientMap)
		featureInvokeCallLog.Info(transientData)
		// eventError := stub.SetEvent("FeatureCreatedEvent", transientData)

		// TODO: Meaningful Event Payload
		eventPayload := "Created Feature: " + featureId
		payloadAsBytes := []byte(eventPayload)
		eventError := stub.SetEvent("FeatureCreatedEvent", payloadAsBytes)
		if eventError != nil {
			featureInvokeCallLog.Error("Error in event Creation: " + eventError.Error())
			return shim.Error(eventError.Error())
		} else {
			featureInvokeCallLog.Info("Event Create Feature OK")
		}
		// ==== Feature saved and indexed. Return success ====
		featureInvokeCallLog.Info("FeatureId: " + feature.FeatureId + ", Name: " + feature.Name + " Succesfully Created - End Create Feature")
		return shim.Success(nil)
	}
}

// ========================================================================================================================
// Modify Feature Name - wrapper of ModifyAgentAddress called from chiancode's Invoke
// ========================================================================================================================
func ModifyFeatureName(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0            1
	// "featureId", "newFeatureName"
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

	featureId := args[0]
	newFeatureName := args[1]

	// ==== get the feature ====
	feature, getError := a.GetFeatureNotFoundError(stub, featureId)
	if getError != nil {
		featureInvokeCallLog.Info("Failed to find feature by id " + featureId)
		featureInvokeCallLog.Error(getError.Error())
		return shim.Error(getError.Error())
	}

	// ==== modify the feature ====
	modifyError := a.ModifyFeatureName(feature, newFeatureName, stub)
	if modifyError != nil {
		featureInvokeCallLog.Info("Failed to modify the feature name: " + newFeatureName)
		featureInvokeCallLog.Error(modifyError.Error())
		return shim.Error(modifyError.Error())
	}
	featureInvokeCallLog.Infof("Feature: " + feature.Name + " modified - end modify feature")

	return shim.Success(nil)
}

// =====================================================================================================================
// Query Feature Not Found Error - wrapper of GetFeatureNotFoundError called from the chaincode invoke
// =====================================================================================================================
func QueryFeatureNotFoundError(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "FeatureId"
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

	featureId := args[0]

	// ==== get the feature ====
	feature, err := a.GetFeatureNotFoundError(stub, featureId)
	if err != nil {
		featureInvokeCallLog.Info("Failed to find feature by id " + featureId)
		featureInvokeCallLog.Error(err.Error())
		return shim.Error(err.Error())
	} else {
		featureInvokeCallLog.Info("Feature ID: " + feature.FeatureId + ", Feature: " + feature.Name + " found")
		// ==== Marshal the byFeature query result ====
		featureAsJSON, err := json.Marshal(feature)
		if err != nil {
			featureInvokeCallLog.Error(err.Error())
			return shim.Error(err.Error())
		}
		return shim.Success(featureAsJSON)
	}
}

// =====================================================================================================================
// Query Feature - wrapper of GetFeature called from the chaincode invoke
// =====================================================================================================================
func QueryFeature(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "FeatureId"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		featureInvokeCallLog.Error(argumentSizeError.Error())
		return shim.Error(argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		featureInvokeCallLog.Error(sanitizeError.Error())
		return shim.Error(sanitizeError.Error())
	}

	featureId := args[0]

	// ==== get the feature ====
	feature, err := a.GetFeature(stub, featureId)
	if err != nil {
		featureInvokeCallLog.Info("Failed to find feature by id " + featureId)
		featureInvokeCallLog.Error(err.Error())
		return shim.Error(err.Error())
	} else {
		featureInvokeCallLog.Info("Feature ID: " + feature.FeatureId + ", Feature: " + feature.Name + " found")
		// ==== Marshal the byFeature query result ====
		featureAsJSON, err := json.Marshal(feature)
		if err != nil {
			featureInvokeCallLog.Error(err.Error())
			return shim.Error(err.Error())
		}
		return shim.Success(featureAsJSON)
	}
}

// ========================================================================================================================
// Query by  - wrapper of GetByFeatureName called from chiancode's Invoke
// ========================================================================================================================
func QueryByFeatureName(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	//   0
	// "FeatureName"
	argumentSizeError := arglib.ArgumentSizeVerification(args, 1)
	if argumentSizeError != nil {
		featureInvokeCallLog.Error(argumentSizeError.Error())
		return shim.Error(argumentSizeError.Error())
	}

	// ==== Input sanitation ====
	sanitizeError := arglib.SanitizeArguments(args)
	if sanitizeError != nil {
		fmt.Print(sanitizeError)
		featureInvokeCallLog.Error(sanitizeError.Error())
		return shim.Error(sanitizeError.Error())
	}

	featureName := args[0]

	// ==== Run the byFeatureName query ====
	byFeatureNameQueryIterator, err := a.GetByFeatureName(featureName, stub)
	if err != nil {
		featureInvokeCallLog.Info("Failed to get feature by name: " + featureName)
		featureInvokeCallLog.Error(err.Error())
		return shim.Error(err.Error())
	}
	if byFeatureNameQueryIterator != nil {
		featureInvokeCallLog.Info(&byFeatureNameQueryIterator)
	}

	// ==== Get the Features for the byFeatureName query result ====
	featuresSlice, err := a.GetFeatureSliceFromRangeQuery(byFeatureNameQueryIterator, stub)
	if err != nil {
		featureInvokeCallLog.Error(err.Error())
		return shim.Error(err.Error())
	}

	// ==== Marshal the byFeatureName query result ====
	featuresByNameAsBytes, err := json.Marshal(featuresSlice)
	if err != nil {
		featureInvokeCallLog.Error(err.Error())
		return shim.Error(err.Error())
	}
	featureInvokeCallLog.Info(featuresByNameAsBytes)

	stringOut := string(featuresByNameAsBytes)
	if stringOut == "null" {
		featureInvokeCallLog.Error("Doesn't exist a feature with the name: " + featureName)
		return shim.Error("Doesn't exist a feature with the name: " + featureName)
	}

	// ==== Return success with featuresByNameAsBytes as payload ====
	return shim.Success(featuresByNameAsBytes)
}
