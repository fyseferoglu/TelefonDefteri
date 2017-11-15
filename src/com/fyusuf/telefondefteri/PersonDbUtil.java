package com.fyusuf.telefondefteri;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class PersonDbUtil {

	private static PersonDbUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/telefon_defteri";

	// Singleton Pattern
	public static PersonDbUtil getInstance() throws Exception {
		if (instance == null)
			instance = new PersonDbUtil();

		return instance;
	}

	private PersonDbUtil() throws Exception {
		dataSource = getDataSource();
	}

	private DataSource getDataSource() throws NamingException {
		
		Context context = new InitialContext();		
		DataSource theDataSource = (DataSource) context.lookup(jndiName);

		return theDataSource;
	}

	public List<Person> getPersonList() throws Exception {
		List<Person> personList = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;

		try {
			myConn = getConnection();

			String sql = "select * from person order by last_name";

			myStmt = myConn.createStatement();

			myRs = myStmt.executeQuery(sql);

			while (myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				String phoneNumber = myRs.getString("phone_number");

				Person person = new Person(id, firstName, lastName, email, phoneNumber);

				personList.add(person);
			}

			return personList;
		} finally {
			close(myConn, myStmt, myRs);
		}
	}

	public void addPerson(Person person) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "insert into person (id, first_name, last_name, email, phone_number) values (PERSON_SEQ.NEXTVAL, ?, ?, ?, ?)";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setString(1, person.getFirstName());
			myStmt.setString(2, person.getLastName());
			myStmt.setString(3, person.getEmail());
			myStmt.setString(4, person.getPhoneNumber());
			myStmt.execute();
		} finally {
			close(myConn, myStmt);
		}
	}

	public Person getPerson(int personId) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;

		try {
			myConn = getConnection();

			String sql = "select * from person where id=?";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setInt(1, personId);

			myRs = myStmt.executeQuery();

			Person person = null;

			if (myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				String phoneNumber = myRs.getString("phone_number");
				person = new Person(id, firstName, lastName, email, phoneNumber);
			} else {
				throw new Exception("Could not find person id: " + personId);
			}

			return person;
		} finally {
			close(myConn, myStmt, myRs);
		}
	}

	public void updatePerson(Person person) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "update person " + " set first_name=?, last_name=?, email=?, phone_number=?" + " where id=?";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setString(1, person.getFirstName());
			myStmt.setString(2, person.getLastName());
			myStmt.setString(3, person.getEmail());
			myStmt.setString(4, person.getPhoneNumber());
			myStmt.setInt(5, person.getId());

			myStmt.execute();
		} finally {
			close(myConn, myStmt);
		}
	}

	public void deletePerson(int personId) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "delete from person where id=?";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setInt(1, personId);

			myStmt.execute();
		} finally {
			close(myConn, myStmt);
		}
	}
	
	public List<Person> searchPerson(String key) throws Exception {
		List<Person> personList = new ArrayList<>();

		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;

		try {
			myConn = getConnection();
			key = key.toLowerCase();
			String sql = "select * from person where lower(first_name) like ? or lower(last_name) like ? or lower(email) like ? or lower(phone_number) like ?" ;

			myStmt = myConn.prepareStatement(sql);
			myStmt.setString(1, "%" + key + "%");
			myStmt.setString(2, "%" + key + "%");
			myStmt.setString(3, "%" + key + "%");
			myStmt.setString(4, "%" + key + "%");
			myRs = myStmt.executeQuery();

			while (myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				String phoneNumber = myRs.getString("phone_number");

				Person person = new Person(id, firstName, lastName, email, phoneNumber);

				personList.add(person);
			}

			return personList;
		} finally {
			close(myConn, myStmt, myRs);
		}
	}

	private Connection getConnection() throws Exception {
		Connection theConn = dataSource.getConnection();

		return theConn;
	}

	private void close(Connection theConn, Statement theStmt) {
		close(theConn, theStmt, null);
	}

	private void close(Connection theConn, Statement theStmt, ResultSet theRs) {
		try {
			if (theRs != null)
				theRs.close();

			if (theStmt != null)
				theStmt.close();

			if (theConn != null)
				theConn.close();

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	
}
