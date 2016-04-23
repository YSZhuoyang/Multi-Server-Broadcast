package Message;

public class AuthFailMsg extends JsonMessage
{
	private String info = "";

	public AuthFailMsg()
	{
		command = "AUTHTENTICATION_FAIL";
	}
	
	public void setInfo(String i)
	{
		info = i;
	}
}