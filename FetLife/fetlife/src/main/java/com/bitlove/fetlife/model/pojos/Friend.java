package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

@Table(database = FetLifeDatabase.class)
public class Friend extends Member {

//    @JsonIgnore
//    private transient ModelAdapter modelAdapter;
//
//    @JsonProperty("id")
//    @Column
//    @PrimaryKey(autoincrement = false)
//    private String id;
//
//    @JsonProperty("nickname")
//    @Column
//    private String nickname;
//
//    @JsonProperty("avatar")
//    private Avatar avatar;
//
//    @JsonProperty("url")
//    @Column
//    private String link;
//
//    @JsonIgnore
//    @Column
//    private String avatarLink;
//
//    @JsonProperty("meta_line")
//    @Column
//    private String metaInfo;

}
