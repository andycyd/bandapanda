package pxc.bandapanda;

public class User {

	
	    
		private int ID;
		private String token;
		private static User instance;
		private String user;
		
		static{
			instance = new User();
		}
		
		private User(){
		}
		
		public static User getInstance(){
			return User.instance;
		}
		
		public int getId(){
	    	return ID;
	    }
		
		public String getToken(){
			return token;
		}
		
		public String getUser(){
			return user;
		}
		
		public void setId(int id2){
			this.ID = id2;
		}
		
		public void setToken(String token2){
			this.token = token2;
		}
		
		public void setUser(String user2){
			this.user = user2;
		}
		
	}


