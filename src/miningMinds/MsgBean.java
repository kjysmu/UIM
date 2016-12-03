package miningMinds;

public class MsgBean {
	
	int fold;
	
	String msg;
	String id;
	String time;
	
	String cat1;
	String subcat1;
	String cat2;
	String subcat2;
	
	public MsgBean(){
		
		
	}
	
	public void addMsg(String msg){
		this.msg = msg;
	}
	public void addId(String id){
		this.id = id;
	}
	public void addTime(String time){
		this.time = time;
	}
	public void addCat1(String cat1){
		this.cat1 = cat1;
	}
	public void addSubcat1(String subcat1){
		this.subcat1 = subcat1;
	}
	public void addCat2(String cat2){
		this.cat2 = cat2;
	}
	public void addSubcat2(String subcat2){
		this.subcat2 = subcat2;
	}
	
	public void addfold(int fold){
		this.fold = fold;
	}
	
	
	public String getMsg(){
		return msg;
	}
	public String getId(){
		return id;
	}
	public String getTime(){
		return time;
	}
	public String getCat1(){
		return cat1;
	}
	public String getSubcat1(){
		return subcat1;
	}
	public String getCat2(){
		return cat2;
	}
	public String getSubcat2(){
		return subcat2;
	}
	
	public int getfold(){
		return fold;
	}
	
	
	
	
	
	
	
	
	

}
