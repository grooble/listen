package com.grooble.model;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;

import BCrypt.BCrypt;


/*
*	lookup, verify と addMember メソッド
*	lookupはメールだけを使ってデータベースを調べる。そのメールを使っているメンバーがいるかどうかなど。
*	verifyはログインするために使う。
*	addMemberは新しいメンバーをデータベースに入れるために使う
*/

public class Member {
	private Connection conn;
	private EncryptionProtocol encryptor = new EncryptionProtocol();
	private static final String TAG = "Member ";
	private static final String alphanumeric =
	        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	        + "abcdefghijklmnopqrstuvwxyz"
	        + "1234567890"
	        + "!#$%&()+*<>?_-=^~|";
	private static SecureRandom rnd = new SecureRandom();
	
	public Person verify(DataSource ds, 
			Integer id){
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		//MySQL クエリー
		String selectQry = 
			"SELECT stdid, firstname, lastname, email, profilepic, points, fcm_token " +
			"FROM students WHERE (stdid=?)";
		Person person = new Person();

		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(selectQry);
			ps.setInt(1, id);
			System.out.println("Member-->PreparedStatementB: " + ps.toString());
			
			rs = ps.executeQuery();

			if(!rs.next()){
				person = null;
			}
			else {
				do {
					person.setId(rs.getInt(1));
					person.setFirstName(rs.getString(2));
					person.setLastName(rs.getString(3));
					person.setEmail(rs.getString(4));
					person.setProfilePic(rs.getString(5));
					person.setPoints(rs.getInt(6));
					String fcm_token = rs.getString(7);
					// fcm_token may be null if it is a web user rather than android user
					if(rs.wasNull()){
					    fcm_token = "";
					}
					person.setFcm_token(fcm_token);
				} while (rs.next());
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (rs != null) rs.close();} catch (SQLException e) {}
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
		
		return person;
	}
	

	/*
	 * This method checks for a yes/no existence of an email in the database
	 * no user is returned and hash table collisions not checked
	 */
    public boolean verify(DataSource ds, String mail){
        
        // Create hash of mail for lookup
        String hashedMail = String.valueOf(mail.hashCode());
        boolean found = false;
        
        ResultSet rs = null;
        Statement stmt = null;
        PreparedStatement ps = null;
//      MySQL クエリー
        String selectQry = 
            "SELECT email_hash " +
            "FROM students WHERE (email_hash=?)";
        
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("USE teacher");
            ps = conn.prepareStatement(selectQry);
            ps.setString(1, hashedMail);
            System.out.println("Member-->PreparedStatementD: " + ps.toString());
            rs = ps.executeQuery();
            
            if(rs.isBeforeFirst()){
                found = true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (rs != null) rs.close();} catch (SQLException e) {}
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (ps != null) ps.close();} catch (SQLException e ) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }
        System.out.println(TAG + "verify->found: " + found);
        return found;
    }
    
    
    //TODO implement FB login
    public Person verifyFB(DataSource ds, String fbid){
        Person p = new Person();
        return p;
    }
    

	/*
	 * This method is used in the login lookup
	 * Looks up user on the hashed_email field and 
	 * may return more than one user if there is a hash table collision
	 */
	public Person verify(DataSource ds, String mail, String password){
	    
        // Create hash of mail for lookup
        String hashedMail = String.valueOf(mail.hashCode());
        
        List<Person> results = null;
	    
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement ps = null;
//		MySQL クエリー
		String selectQry = 
			"SELECT stdid, " +
		    "firstname, " +
			"lastname, " + 
		    "email, " + 
			"surrogate_key, " +
		    "profilepic, " +
			"points, " +
		    "tutorial, " +
			"fcm_token " +
			"FROM students WHERE (email_hash=?)";
		Person person = new Person();
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(selectQry);
			ps.setString(1, hashedMail);
			System.out.println(TAG + "verify->PStmt: " + ps.toString());
			rs = ps.executeQuery();
			
			if(!rs.next()){
				person = null;
				System.out.println(TAG + "verify->PStmt: member not found");
			}
			else {
			    results = new ArrayList<Person>();
				do {
					person.setId(rs.getInt(1));            // not encrypted
					person.setFirstName(rs.getString(2));  // encrypted
					person.setLastName(rs.getString(3));   // encrypted
					person.setEmail(rs.getString(4));      // encrypted
					person.setSurrogate(rs.getString(5));
					person.setPassword(password);   // use password that was passed to method
					person.setProfilePic(rs.getString(6)); // url encrypted
					person.setPoints(rs.getInt(7));        // not encrypted
					person.setTutorial(rs.getBoolean(8));  // not encrypted
					String fcm_token = rs.getString(9);    // not encrypted
					// fcm_token may be null if it is a web user rather than android user
					if(rs.wasNull()){
					    fcm_token = "";
					}
					person.setFcm_token(fcm_token);
					results.add(person);
				} while (rs.next());
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (rs != null) rs.close();} catch (SQLException e) {}
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}
		
		Person personToReturn = null;

		if(null == results){
		    personToReturn = null;
		}
		else{
		    // handle hash collision where more than one result is returned from the DB
		    // and decrypt the returned person
		    
		    // get SecretKey
		    SecretKey secret = encryptor.getKeyFromString(person.getSurrogate());
		    if (results.size() > 1){
		        personToReturn = this.getDecryptedPerson(deCollide(results, mail, secret), secret);
		    }
		    else{
		        personToReturn = this.getDecryptedPerson(results.get(0), secret);	
		    }
		}
		System.out.println(TAG + "verify->person->email, points: " + personToReturn.getEmail() + ", " + 
		                   personToReturn.getPoints());
		
		return personToReturn;
	}
	
	
	// TODO create convenience method for use in getRecovery
	private Person verifyWithHash(DataSource ds, String emailHash){
	    Person p = new Person();
	    return p;
	}

    // This method is used in to lookup friends to get the id for friending
    // Returns only the id of the user (which is unencrypted).
    public Person lookup(DataSource ds, String mail){
        
        // Create hash of mail for lookup
        String hashedMail = String.valueOf(mail.hashCode());
        
        List<Person> results = null;
        
        ResultSet rs = null;
        Statement stmt = null;
        PreparedStatement ps = null;
//      MySQL クエリー
        String selectQry = 
            "SELECT stdid " +
            "FROM students WHERE (email_hash=?)";
        Person person = new Person();
        
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("USE teacher");
            ps = conn.prepareStatement(selectQry);
            ps.setString(1, hashedMail);
            System.out.println("Member-->PreparedStatement(lookup): " + ps.toString());
            rs = ps.executeQuery();
            
            if(!rs.next()){
                person = null;
                System.out.println("Member-->PreparedStatement(lookup): member not found");
            }
            else {
                results = new ArrayList<Person>();
                do {
                    person.setId(rs.getInt(1));            // not encrypted
                    results.add(person);
                } while (rs.next());
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (rs != null) rs.close();} catch (SQLException e) {}
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (ps != null) ps.close();} catch (SQLException e ) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }
        
        if(null == results){
            return null;
        }
        else{
            // handle hash collision where more than one result is returned from the DB
            if (results.size() > 1){
                //return deCollide(results, mail, password);
                return results.get(0);
            }
            else{
                return results.get(0);  
            }           
        }
    }


    /*
     * Add new member to database.
     * Add hashed password, create random surrogate password and store.
     * Create a secondary locking key from user privacy question.
     * Xor the surrogate key with the secondary locking key and store that as a backup key.
     */
    public void addMember(DataSource ds, 
								String mail, String password, String recoveryAnswer){
        Statement stmt = null;
		PreparedStatement ps = null;
		
        // Create password hash to add to DB
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Generate surrogate encryption key from random string
        String rand = randomString(24);
        SecretKey surrogateKey = encryptor.getKey(rand);
        String surrogateKeyString =
                DatatypeConverter.printBase64Binary(surrogateKey.getEncoded());
        String lockingKeyString = "";
        String backupKeyString = "";
        
        // check for recovery answer and initialize recoverable
        // if there is no answer, the password will not be recoverable
        String answer = "";
        int recoverable = 0;
        if((recoveryAnswer != null) && (recoveryAnswer.length()>0)){
            answer = recoveryAnswer.toUpperCase();
            lockingKeyString = 
                    DatatypeConverter.printBase64Binary(encryptor.getKey(answer).getEncoded());
            recoverable = 1;

            // XOR surrogate key with locking key to store as backup key
            byte[] backupByteArray = encryptor.xorWithKey(
                    DatatypeConverter.parseBase64Binary(surrogateKeyString), 
                    DatatypeConverter.parseBase64Binary(lockingKeyString)
                    );
            backupKeyString = DatatypeConverter.printBase64Binary(backupByteArray);
        }
        
        // Encrypt and hash email
        String encryptedMail = encryptor.encryptWithKey(mail, surrogateKey);
        // Create hash of mail for lookup
        String hashedMail = String.valueOf(mail.hashCode());


		//			MySQLのインサートクエリー
		String ins1 = "INSERT INTO students(email, email_hash, password, tutorial, surrogate_key, backup_key, recoverable) " ;
		String ins2 = "VALUES (?, ?, ?, 1, ?, ?, ?)";
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			String insertQry = ins1 + ins2;
			ps = conn.prepareStatement(insertQry);
			
			ps.setString(1, encryptedMail);
			ps.setString(2, hashedMail);
			ps.setString(3, hashedPassword);
			ps.setString(4, surrogateKeyString);
			ps.setString(5, backupKeyString);
            ps.setInt(6, recoverable);
			System.out.println("Member-->PreparedStatementE: " + ps.toString());

			ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
	}

    // used to add person from pending table when confirmation email verified
	public Person addMember(DataSource ds, 
			String email, String firstname, String lastname, String fbid){
		Statement stmt = null;
		PreparedStatement ps = null;
		
//			MySQLのインサートクエリー
		String ins1 = 
			"INSERT INTO students(email, firstname, lastname, fbid, profilepic) " ;
		String ins2 = "VALUES (?, ?, ?, ?, ?)";
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			String insertQry = ins1 + ins2;
			ps = conn.prepareStatement(insertQry);
			ps.setString(1, email.toLowerCase());
			ps.setString(2, firstname);
			ps.setString(3, lastname);
			ps.setString(4, fbid);
			ps.setString(5, "http://graph.facebook.com/" + fbid + "/picture?type=large");
			System.out.println("Member-->PreparedStatementF: " + ps.toString());

			ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
		}
		finally {
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
		
		try{
			synchronized(this){
				this.wait(200);
			}
		}
		catch(Exception e){e.printStackTrace();}
		
		ResultSet rs = null;
		Statement stmt2 = null;
		PreparedStatement ps2 = null;
		//MySQL クエリー
		String selectQry = 
			"SELECT stdid, firstname, lastname, email, profilepic, points " +
			"FROM students WHERE (fbid=?)";
		Person person = new Person();

		try{
			conn = ds.getConnection();
			stmt2 = conn.createStatement();
			stmt2.executeUpdate("USE teacher");
			ps2 = conn.prepareStatement(selectQry);
			ps2.setString(1, fbid);
			System.out.println("Member-->PreparedStatement-2G: " + ps2.toString());

			rs = ps2.executeQuery();

			if(!rs.next()){
				person = null;
				System.out.println("Member->addMember: set person to null");
			}
			else {
				do {
					person.setId(rs.getInt(1));
					person.setFirstName(rs.getString(2));
					person.setLastName(rs.getString(3));
					person.setEmail(rs.getString(4));
					person.setProfilePic(rs.getString(5));
					person.setPoints(rs.getInt(6));
				} while (rs.next());
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (rs != null) rs.close();} catch (SQLException e) {}
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
		
		return person;
		
}

	
	/*
	 *  TODO encryption protocol needs to be updated to use surrogate key,
	 *  locking key and 2nd locking key to facilitate password recovery
	 */
	public Person updatePwd(DataSource ds, String email, String newPassword){
		
		Statement stmt = null;
		PreparedStatement ps = null;
		
		//			MySQLのインサートクエリー
		String update = "UPDATE students SET password=? WHERE email_hash=?";
	    String pwd1Hash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
	    String emailHash = String.valueOf(email.hashCode());


		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			String insertQry = update;
			ps = conn.prepareStatement(insertQry);
			ps.setString(1, pwd1Hash);
			ps.setString(2, emailHash);
			System.out.println("Member-->PreparedStatementH: " + ps.toString());

			ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
		Person p = this.verify(ds, email, newPassword);
		return p;
	}

/*
 * *************
 */
	/*
	 *  Edit name and other details
	 *  TODO implement encryption
	 */
	public Person updateName(DataSource ds, Person person){
		Statement stmt = null;
		PreparedStatement ps = null;
		
		// get user parameters
		String email = person.getEmail();
		String password = person.getPassword();
		String surrogate = person.getSurrogate();
		String dob = person.getDOB();
		String fname = person.getFirstName();
		String lname = person.getLastName();
		
		Date myDate = null;

		// parse dob to sqldate
		java.sql.Date sqlDate = null;
		String[] dateObjects;
		if((dob != null) && (dob.length() > 0)){
		    dateObjects = dob.split("/");
		    if(dateObjects.length == 3){		        
		        int month = Integer.parseInt(dateObjects[0]);
		        int day = Integer.parseInt(dateObjects[1]);
		        int year = Integer.parseInt(dateObjects[2]);
		        String dateFormatString = "MM/dd/yy";
		        SimpleDateFormat df = new SimpleDateFormat(dateFormatString);
		        try {
		            myDate = (Date) df.parse(dob);
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
		        sqlDate = new java.sql.Date(myDate.getTime());
		    }
		} 

		SecretKey secret = encryptor.getKeyFromString(surrogate);
		//			MySQLのインサートクエリー
		// Check if dob is null, and if so, only update name
		// otherwise update name and date of birth.
		String update = "";
		if(dob != null){
		    update = 
		            "UPDATE students SET firstname=?, lastname=?, date_of_birth=? WHERE email_hash=?";
		    System.out.println("Member--> dob is null");
		}
		else {
		    update = "UPDATE students SET firstname=?, lastname=? WHERE email_hash=?";
		}
		
		if(fname == null){fname = "";}
		if(lname == null){lname = "";}
		System.out.println(TAG + "addName->fname: " + fname + " lname: " + lname);
		System.out.println(TAG + "addName->secret: " + DatatypeConverter.printBase64Binary(secret.getEncoded()));

		// encrypted first and last names
		String cypherFirstName = encryptor.encryptWithKey(fname, secret);
		String cypherLastName = encryptor.encryptWithKey(lname, secret);
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			String insertQry = update;
			ps = conn.prepareStatement(insertQry);
			ps.setString(1, cypherFirstName);
			ps.setString(2, cypherLastName);
			// only set dob if present in parameters
			if(dob != null){
			    ps.setDate(3, myDate);
			    ps.setString(4, email.toLowerCase());			    
			}
			else{
                ps.setString(3, String.valueOf(email.toLowerCase().hashCode()));               			    
			}
			System.out.println("Member-->Updating name..." + ps.toString());

			ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		

		Person p = this.verify(ds, email, password);
		System.out.println(TAG + "fname: " + p.getFirstName() + "; lname: " + p.getLastName());
        return p;
	}
	
	
	
	public Integer testCount(DataSource ds, int id){
//	   System.out.println("testCount called");	
	   Integer tCount = null;
	   Statement stmt = null;
	   PreparedStatement ps = null;
	   ResultSet rs = null;
	   String selectQry = 
		   "SELECT cast(COUNT(test_id)/5 as unsigned)AS test_count " +
	   		"FROM tests WHERE user = ?";

	   try{
		   conn = ds.getConnection();
		   stmt= conn.createStatement();
		   ps = conn.prepareStatement(selectQry);
		   ps.setInt(1, id);
		   System.out.println("Member-->PreparedStatementJ: " + ps.toString());

		   stmt.executeUpdate("USE teacher");
		   rs = ps.executeQuery();
		   
		   if(!rs.next()){
		      tCount = new Integer(0);
		   }
		   else {
			   do {
				   int i = rs.getInt(1);
				   tCount = new Integer(i);
				} while (rs.next());
			}
	   }
	   catch(Exception ex){
		ex.printStackTrace();
	   }
	   finally {
		   try {if (rs != null) rs.close();} catch (SQLException e) {}
		   try {if (stmt != null) stmt.close();} catch (SQLException e) {}
		   try {if (ps != null) ps.close();} catch (SQLException e) {}
		   try {if (conn != null) conn.close();} catch (SQLException e) {}
	   }		
	return tCount;
	}

	
	public void setConfirm(DataSource ds, 
			String email, String password, 
			String code, String confPass){
		
		Statement stmt = null;
		PreparedStatement ps = null;
		
		//			MySQLのインサートクエリー
		String ins1 = 
			"INSERT INTO approval(email, password, code, conf_code)";
		String ins2 = "VALUES (?, ?, ?, ?)";
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			String insertQry = ins1 + ins2;
			ps = conn.prepareStatement(insertQry);
			ps.setString(1, email.toLowerCase());
			ps.setString(2, password);
			ps.setString(3, code);
			ps.setString(4, confPass);
			System.out.println("Member-->PreparedStatementK: " + ps.toString());

			ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
	}
	
	
	public void deleteConfirm(DataSource ds, String code){
		Statement stmt = null;
		PreparedStatement ps = null;
		//MySQL クエリー
		String selectQry = 
			"DELETE FROM approval " +
			"WHERE code=?";

		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(selectQry);
			ps.setString(1, code);
			System.out.println("Member-->PreparedStatementL: " + ps.toString());

			ps.executeUpdate();

		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}				
	}
	
	public Person getConfirm(DataSource ds, String code, String confPass){

		Person person = new Person();
		
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		//MySQL クエリー
		String selectQry = 
			"SELECT firstname, lastname, " +
			"email, password, date_of_birth " +
			"FROM approval WHERE ((code=?) AND (conf_code=?))";

		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(selectQry);
			ps.setString(1, code);
			ps.setString(2, confPass);
			System.out.println("Member-->PreparedStatementM: " + ps.toString());

			rs = ps.executeQuery();

			if(!rs.next()){
				person = null;
			}
			else {
				do {
					person.setFirstName(rs.getString(1));
					person.setLastName(rs.getString(2));
					person.setEmail(rs.getString(3));
					person.setPassword(rs.getString(4));
					person.setDOB(rs.getString(5));
				} while (rs.next());
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (rs != null) rs.close();} catch (SQLException e) {}
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
		
		return person;
	}
	
	//----------------------------------------
	public void setRecovery(DataSource ds, 
			String email, 
			String code, String confPass){
		
		Statement stmt = null;
		PreparedStatement ps = null;
		
		//			MySQLのインサートクエリー
		String ins1 = 
			"INSERT INTO pwdrecover(email_hash, code, conf_code)";
		String ins2 = "VALUES (?, ?, ?)";
		
		// Get hash of the email
		String emailHash = String.valueOf(email.hashCode());

		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			String insertQry = ins1 + ins2;
			ps = conn.prepareStatement(insertQry);
			ps.setString(1, emailHash);
			ps.setString(2, code);
			ps.setString(3, confPass);
			System.out.println("Member-->PreparedStatementN: " + ps.toString());

			ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
	}
	
	public void deleteRecovery(DataSource ds, String email){
		Statement stmt = null;
		PreparedStatement ps = null;
		//MySQL クエリー
		// TODO encrypt pwdrecover table
		// TODO add hashed email column to pwdrecover table
		String selectQry = 
			"DELETE FROM pwdrecover " +
			"WHERE email_hash=?";

	    String emailHash = String.valueOf(email.hashCode());

		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(selectQry);
			ps.setString(1, emailHash);
			System.out.println("Member-->PreparedStatementO: " + ps.toString());

			ps.executeUpdate();

		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}				
	}
	
	public Person getRecovery(DataSource ds, String code, String confPass){
		
		Person p = null;
		String emailHash = "";
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		//MySQL クエリー
		String selectQry = 
			"SELECT email_hash FROM pwdrecover WHERE ((code=?) AND (conf_code=?))";

		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(selectQry);
			ps.setString(1, code);
			ps.setString(2, confPass);
			System.out.println("Member-->PreparedStatementP: " + ps.toString());

			rs = ps.executeQuery();

			if(!rs.next()){
				emailHash = "";
			}
			else {
				do {
					emailHash = rs.getString(1); 
				} while (rs.next());
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (rs != null) rs.close();} catch (SQLException e) {}
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e ) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}	
        
		p = this.verifyWithHash(ds, emailHash);
        return p;
	}
	//----------------------------------------
	
	/*
     *  Update tutorial to show or not show on startup
     */
    public boolean updateTutorial(DataSource ds, String email, String password, int tut){
        
        Statement stmt = null;
        PreparedStatement ps = null;
        boolean updated = false;
        
        String hashedEmail = String.valueOf(email.hashCode());
        
        String update = 
                "UPDATE students SET tutorial=? WHERE email_hash=?";
        
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("USE teacher");
            String insertQry = update;
            ps = conn.prepareStatement(insertQry);
            ps.setInt(1, tut);
            ps.setString(2, hashedEmail);
            System.out.println("Member-->Updating tut status..." + ps.toString());

            int rowCount = ps.executeUpdate();
            if(rowCount > 0){
                updated = true;
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (ps != null) ps.close();} catch (SQLException e ) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }
        return updated;
    }
    
    /*
     * The hashed email lookup in verify may return multiple results.
     * decrypt and verify email to determine the correct user and return them.
     */
    private Person deCollide(List<Person> results, String email, SecretKey secret){
        Person p = new Person();
        
        Iterator<Person> it = results.iterator();
        while (it.hasNext()){
            p = it.next();
            String encryptedMail = p.getEmail();
            String clearMail = encryptor.decryptWithKey(encryptedMail, secret);
            if(clearMail.equals(email)){break;}
        }
        return p;
    }

    
    /*
     *  Convenience method to decrypt the fields of a Person object
     *  and return a non-encrypted Person
     */
    private Person getDecryptedPerson(Person person, SecretKey secret){
        
        // Initialize fields that need to be decrypted
        String email, firstName, lastName, dob, fbid, fcm, profilePic = null;
        email = person.getEmail();
        firstName = person.getFirstName();
        lastName = person.getLastName();
        dob = person.getDOB();
        fbid = person.getFbid();
        fcm = person.getFcm_token();
        profilePic = person.getProfilePic();
        
        // Decrypt where the field is not null or empty
        if(!(email == null) && !email.isEmpty()){
            person.setEmail(encryptor.decryptWithKey(email, secret));
        }
        if(!(firstName == null) && !firstName.isEmpty()){
            person.setFirstName(encryptor.decryptWithKey(firstName, secret));
        }
        if(!(lastName == null) && !lastName.isEmpty()){
            person.setLastName(encryptor.decryptWithKey(lastName, secret));
        }
        if(!(dob == null) && !dob.isEmpty()){
            person.setDOB(encryptor.decryptWithKey(dob, secret));
        }
        if(!(fbid == null) && !fbid.isEmpty()){
            person.setDOB(encryptor.decryptWithKey(fbid, secret));
        }
        if(!(profilePic == null) && !profilePic.isEmpty()){
            person.setProfilePic(encryptor.decryptWithKey(profilePic, secret));
        }

        // return decrypted person
        return person;
    }
    
    private String randomString(int length){
        StringBuilder sb = new StringBuilder(length);
        for( int i = 0; i < length; i++ ) 
           sb.append( alphanumeric.charAt( rnd.nextInt(alphanumeric.length()) ) );
        return sb.toString();
    }
	
}