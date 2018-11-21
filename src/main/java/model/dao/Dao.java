package model.dao;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.List;
import java.util.Optional;

/**
 * Dao interface defines an abstract API that performs CRUD operations on objects of type T.
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 * @param <T>
 */
public interface Dao<T> {
  // Tired of Null Pointer Exceptions? Consider Using Java SE 8's Optional!
  // (https://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html
  Optional<T> get(HFClient hfClient, Channel channel, String id);

  List<T> getAll();

  boolean create(HFClient clientHF, User userHF, Channel channel, T type)
      throws ProposalException, InvalidArgumentException;

  boolean update(HFClient clientHF, User userHF, Channel channel, T type, String[] params)
      throws ProposalException, InvalidArgumentException;

  boolean delete(HFClient clientHF, User user, Channel channel, T type)
      throws ProposalException, InvalidArgumentException;
}

