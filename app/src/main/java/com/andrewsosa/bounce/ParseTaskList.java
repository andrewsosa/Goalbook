package com.andrewsosa.bounce;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.UUID;

/**
 * Created by andrewsosa on 4/7/15.
 */
@ParseClassName("List")
public class ParseTaskList extends ParseObject {

    public ParseTaskList() {

    }

    public ParseTaskList(String name) {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
        setUser(ParseUser.getCurrentUser());

        setName(name);

    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    private ParseUser getUser() {
        return getParseUser("user");

    }

    private void setUser(ParseUser user) {
        put("user", user);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ParseQuery<ParseTaskList> getQuery() {
        return ParseQuery.getQuery(ParseTaskList.class)
                .whereEqualTo("user", ParseUser.getCurrentUser());

    }
}
