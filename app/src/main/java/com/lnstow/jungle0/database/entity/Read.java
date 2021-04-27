package com.lnstow.jungle0.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Fts4
@Entity(tableName = "read")
public class Read {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    private int id;
    private byte type;
    private String link;
    private String title;
    private long time;
    private byte status;
    public static final byte STATUS_UNREAD = 1;
    public static final byte STATUS_READING = 2;
    public static final byte STATUS_READ = 3;

    @NonNull
    @Override
    public String toString() {
        return "rowid" + id + "  " + title;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || this.link.equals(((Read) obj).link);
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }

    @Ignore
    public Read() {
    }

    public Read(byte type, String link, String title, long time, byte status) {
        this.type = type;
        this.link = link;
        this.title = title;
        this.time = time;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

}

