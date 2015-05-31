package com.ashish.routofy;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class JsonObjectParse implements Parcelable {

	int userId;
	int id;
	String title;
	String body;

	public JsonObjectParse(int userId, int id, String title, String body) {
		super();
		this.userId = userId;
		this.id = id;
		this.title = title;
		this.body = body;
	}

	public static ArrayList<JsonObjectParse> parseJsonArray(JSONArray array) {
		ArrayList<JsonObjectParse> arraylist = new ArrayList<>();

		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject obj = array.getJSONObject(i);
				int userid = obj.getInt("userId");
				int id = obj.getInt("id");
				String title = obj.getString("title");
				String body = obj.getString("body");

				JsonObjectParse object = new JsonObjectParse(userid, id, title,
						body);

				arraylist.add(object);

			} catch (JSONException e) {
			}
		}

		return arraylist;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	private JsonObjectParse(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        title = in.readString();
        body = in.readString();
    }

	public static final Parcelable.Creator<JsonObjectParse> CREATOR = new Parcelable.Creator<JsonObjectParse>() {
		public JsonObjectParse createFromParcel(Parcel in) {
			return new JsonObjectParse(in);
		}

		public JsonObjectParse[] newArray(int size) {
			return new JsonObjectParse[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(userId);
		out.writeInt(id);
		out.writeString(body);
		out.writeString(title);
	}
}
