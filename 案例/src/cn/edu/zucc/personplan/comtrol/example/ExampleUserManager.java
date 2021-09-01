package cn.edu.zucc.personplan.comtrol.example;

import cn.edu.zucc.personplan.itf.IUserManager;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ExampleUserManager implements IUserManager {

    @Override
    public BeanUser reg(String userid, String pwd, String pwd2) throws BaseException {
        if (userPwdCheck(userid, pwd)) {
            throw new BaseException("账户密码不为空");
        }
        if (!pwd.equals(pwd2)) {
            throw new BaseException("两次密码不同");
        }
        Connection conn = null;
        BeanUser beanUser = new BeanUser();
        try {
            conn= DBUtil.getConnection();
            String sql = "SELECT user_id FROM tbl_user WHERE user_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, userid);
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                throw new BaseException("账号已存在");
            }
            sql = "INSERT INTO tbl_user VALUES (?,?,?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, userid);
            pst.setString(2, pwd);
            Timestamp time = new Timestamp(new java.util.Date().getTime());
            pst.setTimestamp(3, time);
            pst.execute();
            rs.close();
            pst.close();
            return beanUser;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BaseException("数据库错误");
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    private boolean userPwdCheck(String userid, String pwd) {
        return userid == null || userid.equals("") || pwd == null || pwd.equals("");
    }

    @Override
    public BeanUser login(String userid, String pwd) throws BaseException {
        if (userPwdCheck(userid, pwd)) {
            throw new BaseException("账户密码不为空");
        }

        Connection conn = null;
        BeanUser beanUser = new BeanUser();
        try {
            conn= DBUtil.getConnection();
            String sql = "SELECT register_time FROM tbl_user WHERE user_id = ? AND user_pwd = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, userid);
            pst.setString(2, pwd);
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                beanUser.setUser(userid);
                beanUser.setPwd(pwd);
                beanUser.setRegisterTime(rs.getTime(1));
                BeanUser.currentLoginUser = beanUser;
            } else {
                throw new BaseException("密码错误");
            }
            rs.close();
            pst.close();
            return beanUser;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BaseException("数据库错误");
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }


    @Override
    public void changePwd(BeanUser user, String oldPwd, String newPwd,
                          String newPwd2) throws BaseException {
        if (oldPwd == null || oldPwd.equals("") || newPwd == null || newPwd.equals("")) {
            throw new BaseException("密码不为空");
        }
        if (!user.getPwd().equals(oldPwd)) {
            throw new BaseException("密码错误");
        }
        if (!newPwd.equals(newPwd2)) throw new BaseException("两次密码不同");
        Connection conn = null;
        try {
            conn= DBUtil.getConnection();
            String sql = "UPDATE tbl_user SET user_pwd = ? WHERE user_id = ?;";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newPwd);
            pst.setString(2, user.getUser());
            pst.execute();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BaseException("数据库错误");
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

}
