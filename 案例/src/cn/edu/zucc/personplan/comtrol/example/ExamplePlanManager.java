package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil2;

public class ExamplePlanManager implements IPlanManager {

    @Override
    public BeanPlan addPlan(String name) throws BaseException {
        Connection conn = null;
        try {
            conn = DBUtil2.getInstance().getConnection();
            int max = 0;
            String sql = "SELECT MAX(plan_order) FROM tbl_plan WHERE user_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, BeanUser.currentLoginUser.getUser());
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                max = rs.getInt(1);
            }
            sql = "INSERT INTO tbl_plan (user_id,plan_order,plan_name,create_time,step_count,start_step_count,finished_step_count) VALUES (?,?,?,?,0,0,0)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, BeanUser.currentLoginUser.getUser());
            pst.setInt(2, max + 1);
            pst.setString(3, name);
            Timestamp time = new Timestamp(new java.util.Date().getTime());
            pst.setTimestamp(4, time);

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
        return null;
    }

    @Override
    public List<BeanPlan> loadAll() throws BaseException {
        List<BeanPlan> result = new ArrayList<BeanPlan>();
        Connection conn = null;
        try {
            conn = DBUtil2.getInstance().getConnection();
            String sql = "SELECT * FROM tbl_plan";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                BeanPlan beanPlan = new BeanPlan();
                beanPlan.setPlan_id(rs.getInt(1));
                beanPlan.setUser_id(rs.getString(2));
                beanPlan.setPlan_order(rs.getInt(3));
                beanPlan.setPlan_name(rs.getString(4));
                beanPlan.setCreate_time(rs.getTime(5));
                beanPlan.setStep_count(rs.getInt(6));
                beanPlan.setStart_step_count(rs.getInt(7));
                beanPlan.setFinished_step_count(rs.getInt(8));
                result.add(beanPlan);
            }
            rs.close();
            pst.close();
            return result;
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
    public void deletePlan(BeanPlan plan) throws BaseException {
        Connection conn = null;
        try {
            if (plan.getStep_count() > 0){
                throw new BaseException("计划含有步骤，无法删除");
            }
            conn = DBUtil2.getInstance().getConnection();
            conn.setAutoCommit(false);
            String sql = "DELETE FROM tbl_plan WHERE plan_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlan_id());
            pst.execute();

            sql = "UPDATE tbl_plan SET plan_order = plan_order - 1 WHERE plan_order > ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlan_order());
            pst.execute();

            conn.commit();
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
