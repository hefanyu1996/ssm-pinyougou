package cn.itcast.web.controller;

import cn.itcast.pojo.TbUser;
import cn.itcast.service.UserService;
import cn.itcast.utils.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jws.WebResult;
import java.util.Date;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return userService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbUser user) {
        try {
            //创建时间
            user.setCreated(new Date());
            //修改时间
            user.setUpdated(new Date());

            //MD5密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            //注册类型
            user.setSourceType("1");

            userService.add(user);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param user
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbUser user, int page, int rows) {
        return userService.findPage(user, page, rows);
    }


    /**
     * 发送短信验证
     *
     * @param phone
     * @return
     */
    @RequestMapping("/sendSms")
    public Result sendSms(String phone) {

        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            return new Result(false, "手机号码格式有误");
        }

        try {
            //调用业务层发送验证码
            userService.createSmsCode(phone);

            return new Result(true, "短信验证码发送成功");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false, "短信验证码发送失败");
    }

    /**
     * 短信验证码校验
     */
    @RequestMapping("/checkSms")
    public Result checkSms(String phone, String smsCode) {

        if (smsCode != null && !smsCode.equals("")) {

            try {

                if (userService.checkSmsCode(phone, smsCode)) {
                    return new Result(true, "校验成功");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return new Result(false, "验证码错误");
    }

}
