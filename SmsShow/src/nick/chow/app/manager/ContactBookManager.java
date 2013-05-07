package nick.chow.app.manager;

import java.util.Arrays;

import nick.chow.app.context.Tools;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * @author zhouyun
 *
 */
public class ContactBookManager {
	public static final Uri SMS_PROVIDER_URI = Uri.parse("content://sms/");
	private final String tag = getClass().getSimpleName();
	private Context context;
	
	protected ContactBookManager(Context context) {
		this.context = context;
	}

	/**
	 * query user name from contact book by number
	 * @param number
	 * @return
	 */
	public String getNameByNumber(String number) {
		if (number == null) {
			return "N/A";
		}
		String tNumber = number, srcNumber = number, num1 = number, num2 = num1;
		if (number.indexOf("+86") != -1) {
			srcNumber = number.substring(3);
		} else {
			tNumber = "+86" + number;
		}
		
		StringBuilder newNumber = new StringBuilder(srcNumber);
		StringBuilder newNumber2 = new StringBuilder(tNumber);
		if (srcNumber.length() == 11) {
			newNumber.insert(1, " ");
			newNumber.insert(5, "-");
			newNumber.insert(9, "-");
			num1 = newNumber.toString();
			
			newNumber2.insert(3, " ");
			newNumber2.insert(7, "-");
			newNumber2.insert(12, "-");
			num2 = newNumber2.toString();
		}
		
		String cName = queryNameByNumber(srcNumber, tNumber, num1, num2);
		if (cName != null) {
			return cName;
		}
		return number;
	}
	
	public String queryNameByNumber(String... numbers) {
		if (Log.isLoggable(tag, Log.INFO)) {
			Log.i(tag, "queryNameByNumber number = " + Arrays.toString(numbers));
		}
		Tools.show(context, Arrays.toString(numbers));
		if (numbers == null) {
			return null;
		}
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		StringBuilder inClause = new StringBuilder("(");
		for (String number : numbers) {
			inClause.append("'").append(number).append("'").append(",");
		}
		inClause.deleteCharAt(inClause.length() - 1);
		inClause.append(")");
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				projection, // Which columns to return.
				ContactsContract.CommonDataKinds.Phone.NUMBER + " in " + inClause, // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.
		
		Log.i(tag, "cursor = " + cursor);
		if (cursor != null && cursor.moveToFirst()) {
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME); 
			String nme = cursor.getString(nameFieldColumnIndex);
			Log.i(tag, "found! contact name = " + nme);
			cursor.close();
			return nme;
		}
		if (Log.isLoggable(tag, Log.INFO)) {
			Log.i(tag, "no found name by " + Arrays.toString(numbers));
		}
		return null;
	}
	
	public static ContactBookManager getManager(Context context) {
		return new ContactBookManager(context);
	}
}
