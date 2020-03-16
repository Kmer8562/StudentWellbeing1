package com.example.studentwellbeing.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Councillor implements Parcelable {
    private String name,username,password, councillorId;

    public Councillor() {
    }

    protected Councillor(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        councillorId = in.readString();
    }

    public static final Creator<Councillor> CREATOR = new Creator<Councillor>() {
        @Override
        public Councillor createFromParcel(Parcel in) {
            return new Councillor(in);
        }

        @Override
        public Councillor[] newArray(int size) {
            return new Councillor[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCouncillorId() {
        return councillorId;
    }

    public void setCouncillorId(String councillorId) {
        this.councillorId = councillorId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(councillorId);
    }
}
