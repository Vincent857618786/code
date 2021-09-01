package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;

public class ExampleStepManager implements IStepManager {

    @Override
    public void add(BeanPlan plan, String name, String planstartdate,
                    String planfinishdate) throws BaseException {
        Connection conn = null;
        try {
            conn= DBUtil.getConnection();
            int max = 0;
            String sql = "SELECT MAX(step_order) FROM tbl_step WHERE plan_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlan_id());
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                max = rs.getInt(1);
            }

            sql = "INSERT INTO tbl_step (plan_id,step_order,step_name,plan_begin_time,plan_end_time) VALUES (?,?,?,?,?)";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlan_id());
            pst.setInt(2, max + 1);
            pst.setString(3, name);
            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            pst.setDate(4, Date.valueOf(planstartdate));
            pst.setDate(5, Date.valueOf(planfinishdate));

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

    @Override
    public List<BeanStep> loadSteps(BeanPlan plan) throws BaseException {
        List<BeanStep> result = new ArrayList<BeanStep>();
        Connection conn = null;
        try {
            conn= DBUtil.getConnection();
            String sql = "SELECT * FROM tbl_step WHERE plan_id = ? ORDER BY step_order";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, plan.getPlan_id());
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                BeanStep beanStep = new BeanStep();

                beanStep.setStep_id(rs.getInt(1));
                beanStep.setPlan_id(rs.getInt(2));
                beanStep.setStep_order(rs.getInt(3));
                beanStep.setStep_name(rs.getString(4));
                beanStep.setPlan_begin_time(rs.getDate(5));
                beanStep.setPlan_end_time(rs.getDate(6));
                beanStep.setReal_begin_time(rs.getDate(7));
                beanStep.setReal_end_time(rs.getDate(8));
                result.add(beanStep);
            }
            rs.close();
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
        return result;
    }

    @Override
    public void deleteStep(BeanStep step) throws BaseException {
        if (step == null) return;
        Connection conn = null;
        try {
            conn= DBUtil.getConnection();
            conn.setAutoCommit(false);

            String sql = "DELETE FROM tbl_step WHERE step_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStep_id());
            pst.execute();

            sql = "UPDATE tbl_step SET step_order = step_order-1 WHERE plan_id = ? AND step_order > ?";
            pst =  conn.prepareStatement(sql);
            pst.setInt(1,step.getPlan_id());
            pst.setInt(2,step.getStep_order());
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

    @Override
    public void startStep(BeanStep step) throws BaseException {
        if (step == null) return;
        Connection conn = null;
        try {
            conn= DBUtil.getConnection();
            String sql = "UPDATE tbl_step SET real_begin_time = ? WHERE step_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, new Date(new java.util.Date().getTime()));
            pst.setInt(2, step.getStep_id());
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

    @Override
    public void finishStep(BeanStep step) throws BaseException {
        if (step == null) return;
        Connection conn = null;
        try {
            conn= DBUtil.getConnection();
            String sql = "UPDATE tbl_step SET real_end_time = ? WHERE step_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, new Date(new java.util.Date().getTime()));
            pst.setInt(2, step.getStep_id());
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

    @Override
    public void moveUp(BeanStep step) throws BaseException {
        if (step == null) return;
        if (step.getStep_order() == 1) {
            throw new BaseException("已经为第一步");
        }
        Connection conn = null;
        try {
            conn= DBUtil.getConnection();

            String sql = "UPDATE tbl_step SET step_order = step_order+1 WHERE plan_id = ? AND step_order = ?-1;\n";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getPlan_id());
            pst.setInt(2, step.getStep_order());
            pst.execute();

            sql = "UPDATE tbl_step SET step_order = step_order-1 WHERE step_id = ?;";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStep_id());
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

    @Override
    public void moveDown(BeanStep step) throws BaseException {
        if (step == null) return;

        Connection conn = null;
        try {
            int max = 0;
            conn= DBUtil.getConnection();
            String sql = "SELECT MAX(step_order) from tbl_step WHERE plan_id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getPlan_id());
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                max = rs.getInt(1);
            }
            if (step.getStep_order() == max) {
                throw new BaseException("已经为最后一步");
            }
            sql = "UPDATE tbl_step SET step_order = step_order-1 WHERE plan_id = ? AND step_order = ?+1;\n";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getPlan_id());
            pst.setInt(2, step.getStep_order());
            pst.execute();

            sql = "UPDATE tbl_step SET step_order = step_order+1 WHERE step_id = ?;";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, step.getStep_id());
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
