package com.teamtreehouse.todotoday.dao;

import com.teamtreehouse.todotoday.model.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//Normally the @Repository is not on the DAO but on the implementation
//If you enabled the JPA repository, and extend CrudRepository, in the dataConfig Spring data will generate the implementing classes for you
@Repository
public interface TaskDao extends CrudRepository<Task, Long> {//Task = the class you want the CRUDRepo to deal with, and the ID type, in this case Long
    //if you need specific methods I would just have to run them
    /*

    Query Methods with Spring Data JPA

    You can add to CRUD functionality in a Spring Data JPA interface by writing intuitive method stubs. For example, if you have a repository for Contact objects that include a persisted email field, you could write a method that queries the datastore for contacts given a certain email address as follows:

    public class ContactDao extends CrudRepository<Contact,Long> {
      Contact findByEmail(String email);
    }
    For more on query method options, check the Spring docs: http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods

    */

    //Gets the principal id from the SecurityConfig look at the method securityExtension
    @Query("select t from Task t where t.user.id=:#{principal.id}")
    List<Task> findAll();


}
