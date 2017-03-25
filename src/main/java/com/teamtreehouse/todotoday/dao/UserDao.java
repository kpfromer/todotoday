package com.teamtreehouse.todotoday.dao;

import com.teamtreehouse.todotoday.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kpfromer on 3/24/17.
 */

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);
    /*
    * This is amazing!
    * Spring Data will create a implementation but also will create a method
    * for findByUsername(String username)
    * It will look for a row in the username column with the parameter username!
    * WE DON'T HAVE TO WRITE THAT OURSELVES!
    * */
}
