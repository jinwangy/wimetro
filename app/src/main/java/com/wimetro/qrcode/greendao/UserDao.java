package com.wimetro.qrcode.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wimetro.qrcode.greendao.entity.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER".
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property App_user = new Property(1, String.class, "app_user", false, "APP_USER");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Tele_no = new Property(3, String.class, "tele_no", false, "TELE_NO");
        public final static Property Hce_id = new Property(4, String.class, "hce_id", false, "HCE_ID");
        public final static Property Email = new Property(5, String.class, "email", false, "EMAIL");
        public final static Property Sex = new Property(6, String.class, "sex", false, "SEX");
        public final static Property Activate_type = new Property(7, String.class, "activate_type", false, "ACTIVATE_TYPE");
        public final static Property Idcard = new Property(8, String.class, "idcard", false, "IDCARD");
        public final static Property Login_flag = new Property(9, String.class, "login_flag", false, "LOGIN_FLAG");
        public final static Property Card_flag = new Property(10, String.class, "card_flag", false, "CARD_FLAG");
        public final static Property Voucher_id = new Property(11, String.class, "voucher_id", false, "VOUCHER_ID");
        public final static Property User_id = new Property(12, String.class, "user_id", false, "USER_ID");
        public final static Property Alipay_user_id = new Property(13, String.class, "alipay_user_id", false, "ALIPAY_USER_ID");
        public final static Property Flag = new Property(14, String.class, "flag", false, "FLAG");
        public final static Property RequestMessage = new Property(15, String.class, "requestMessage", false, "REQUEST_MESSAGE");
        public final static Property Ca_flag = new Property(16, String.class, "ca_flag", false, "CA_FLAG");
        public final static Property Result = new Property(17, String.class, "result", false, "RESULT");
        public final static Property Agreement_no = new Property(18, String.class, "agreement_no", false, "AGREEMENT_NO");
        public final static Property Channel_type = new Property(19, String.class, "channel_type", false, "CHANNEL_TYPE");
        public final static Property Para_1 = new Property(20, String.class, "para_1", false, "PARA_1");
        public final static Property Para_2 = new Property(21, String.class, "para_2", false, "PARA_2");
        public final static Property Para_3 = new Property(22, String.class, "para_3", false, "PARA_3");
        public final static Property Para_4 = new Property(23, String.class, "para_4", false, "PARA_4");
        public final static Property Para_5 = new Property(24, String.class, "para_5", false, "PARA_5");
        public final static Property Para_6 = new Property(25, String.class, "para_6", false, "PARA_6");
    }


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"APP_USER\" TEXT," + // 1: app_user
                "\"NAME\" TEXT," + // 2: name
                "\"TELE_NO\" TEXT," + // 3: tele_no
                "\"HCE_ID\" TEXT," + // 4: hce_id
                "\"EMAIL\" TEXT," + // 5: email
                "\"SEX\" TEXT," + // 6: sex
                "\"ACTIVATE_TYPE\" TEXT," + // 7: activate_type
                "\"IDCARD\" TEXT," + // 8: idcard
                "\"LOGIN_FLAG\" TEXT," + // 9: login_flag
                "\"CARD_FLAG\" TEXT," + // 10: card_flag
                "\"VOUCHER_ID\" TEXT," + // 11: voucher_id
                "\"USER_ID\" TEXT," + // 12: user_id
                "\"ALIPAY_USER_ID\" TEXT," + // 13: alipay_user_id
                "\"FLAG\" TEXT," + // 14: flag
                "\"REQUEST_MESSAGE\" TEXT," + // 15: requestMessage
                "\"CA_FLAG\" TEXT," + // 16: ca_flag
                "\"RESULT\" TEXT," + // 17: result
                "\"AGREEMENT_NO\" TEXT," + // 18: agreement_no
                "\"CHANNEL_TYPE\" TEXT," + // 19: channel_type
                "\"PARA_1\" TEXT," + // 20: para_1
                "\"PARA_2\" TEXT," + // 21: para_2
                "\"PARA_3\" TEXT," + // 22: para_3
                "\"PARA_4\" TEXT," + // 23: para_4
                "\"PARA_5\" TEXT," + // 24: para_5
                "\"PARA_6\" TEXT);"); // 25: para_6
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String app_user = entity.getApp_user();
        if (app_user != null) {
            stmt.bindString(2, app_user);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String tele_no = entity.getTele_no();
        if (tele_no != null) {
            stmt.bindString(4, tele_no);
        }
 
        String hce_id = entity.getHce_id();
        if (hce_id != null) {
            stmt.bindString(5, hce_id);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(6, email);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(7, sex);
        }
 
        String activate_type = entity.getActivate_type();
        if (activate_type != null) {
            stmt.bindString(8, activate_type);
        }
 
        String idcard = entity.getIdcard();
        if (idcard != null) {
            stmt.bindString(9, idcard);
        }
 
        String login_flag = entity.getLogin_flag();
        if (login_flag != null) {
            stmt.bindString(10, login_flag);
        }
 
        String card_flag = entity.getCard_flag();
        if (card_flag != null) {
            stmt.bindString(11, card_flag);
        }
 
        String voucher_id = entity.getVoucher_id();
        if (voucher_id != null) {
            stmt.bindString(12, voucher_id);
        }
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(13, user_id);
        }
 
        String alipay_user_id = entity.getAlipay_user_id();
        if (alipay_user_id != null) {
            stmt.bindString(14, alipay_user_id);
        }
 
        String flag = entity.getFlag();
        if (flag != null) {
            stmt.bindString(15, flag);
        }
 
        String requestMessage = entity.getRequestMessage();
        if (requestMessage != null) {
            stmt.bindString(16, requestMessage);
        }
 
        String ca_flag = entity.getCa_flag();
        if (ca_flag != null) {
            stmt.bindString(17, ca_flag);
        }
 
        String result = entity.getResult();
        if (result != null) {
            stmt.bindString(18, result);
        }
 
        String agreement_no = entity.getAgreement_no();
        if (agreement_no != null) {
            stmt.bindString(19, agreement_no);
        }
 
        String channel_type = entity.getChannel_type();
        if (channel_type != null) {
            stmt.bindString(20, channel_type);
        }
 
        String para_1 = entity.getPara_1();
        if (para_1 != null) {
            stmt.bindString(21, para_1);
        }
 
        String para_2 = entity.getPara_2();
        if (para_2 != null) {
            stmt.bindString(22, para_2);
        }
 
        String para_3 = entity.getPara_3();
        if (para_3 != null) {
            stmt.bindString(23, para_3);
        }
 
        String para_4 = entity.getPara_4();
        if (para_4 != null) {
            stmt.bindString(24, para_4);
        }
 
        String para_5 = entity.getPara_5();
        if (para_5 != null) {
            stmt.bindString(25, para_5);
        }
 
        String para_6 = entity.getPara_6();
        if (para_6 != null) {
            stmt.bindString(26, para_6);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String app_user = entity.getApp_user();
        if (app_user != null) {
            stmt.bindString(2, app_user);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String tele_no = entity.getTele_no();
        if (tele_no != null) {
            stmt.bindString(4, tele_no);
        }
 
        String hce_id = entity.getHce_id();
        if (hce_id != null) {
            stmt.bindString(5, hce_id);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(6, email);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(7, sex);
        }
 
        String activate_type = entity.getActivate_type();
        if (activate_type != null) {
            stmt.bindString(8, activate_type);
        }
 
        String idcard = entity.getIdcard();
        if (idcard != null) {
            stmt.bindString(9, idcard);
        }
 
        String login_flag = entity.getLogin_flag();
        if (login_flag != null) {
            stmt.bindString(10, login_flag);
        }
 
        String card_flag = entity.getCard_flag();
        if (card_flag != null) {
            stmt.bindString(11, card_flag);
        }
 
        String voucher_id = entity.getVoucher_id();
        if (voucher_id != null) {
            stmt.bindString(12, voucher_id);
        }
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(13, user_id);
        }
 
        String alipay_user_id = entity.getAlipay_user_id();
        if (alipay_user_id != null) {
            stmt.bindString(14, alipay_user_id);
        }
 
        String flag = entity.getFlag();
        if (flag != null) {
            stmt.bindString(15, flag);
        }
 
        String requestMessage = entity.getRequestMessage();
        if (requestMessage != null) {
            stmt.bindString(16, requestMessage);
        }
 
        String ca_flag = entity.getCa_flag();
        if (ca_flag != null) {
            stmt.bindString(17, ca_flag);
        }
 
        String result = entity.getResult();
        if (result != null) {
            stmt.bindString(18, result);
        }
 
        String agreement_no = entity.getAgreement_no();
        if (agreement_no != null) {
            stmt.bindString(19, agreement_no);
        }
 
        String channel_type = entity.getChannel_type();
        if (channel_type != null) {
            stmt.bindString(20, channel_type);
        }
 
        String para_1 = entity.getPara_1();
        if (para_1 != null) {
            stmt.bindString(21, para_1);
        }
 
        String para_2 = entity.getPara_2();
        if (para_2 != null) {
            stmt.bindString(22, para_2);
        }
 
        String para_3 = entity.getPara_3();
        if (para_3 != null) {
            stmt.bindString(23, para_3);
        }
 
        String para_4 = entity.getPara_4();
        if (para_4 != null) {
            stmt.bindString(24, para_4);
        }
 
        String para_5 = entity.getPara_5();
        if (para_5 != null) {
            stmt.bindString(25, para_5);
        }
 
        String para_6 = entity.getPara_6();
        if (para_6 != null) {
            stmt.bindString(26, para_6);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // app_user
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // tele_no
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // hce_id
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // email
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // sex
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // activate_type
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // idcard
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // login_flag
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // card_flag
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // voucher_id
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // user_id
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // alipay_user_id
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // flag
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // requestMessage
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // ca_flag
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // result
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // agreement_no
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // channel_type
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // para_1
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // para_2
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // para_3
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // para_4
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // para_5
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25) // para_6
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setApp_user(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTele_no(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setHce_id(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setEmail(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSex(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setActivate_type(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIdcard(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setLogin_flag(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCard_flag(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setVoucher_id(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setUser_id(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setAlipay_user_id(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setFlag(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setRequestMessage(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setCa_flag(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setResult(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setAgreement_no(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setChannel_type(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setPara_1(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setPara_2(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setPara_3(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setPara_4(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setPara_5(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setPara_6(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(User entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
