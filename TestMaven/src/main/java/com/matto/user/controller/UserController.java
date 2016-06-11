package com.matto.user.controller;




import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.matto.user.pojo.TUser;
import com.matto.user.service.IUserService;
import com.matto.util.CookieUtil;
import com.matto.util.MD5;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by matto on 16-6-11.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    protected final transient Log log = LogFactory.getLog(UserController.class);

    @Autowired
    private IUserService userService;

    public UserController() {
    }

    /**
     * 登入
     * @param model
     * @param request
     * @param response
     * @param name
     * @param password
     * @throws IOException
     */
    @RequestMapping(value="/login/", method= RequestMethod.POST)
    public void login(Model model, HttpServletRequest request, HttpServletResponse response,
                      @RequestParam("name") String name,
                      @RequestParam("password") String password) throws IOException {
        response.setCharacterEncoding("utf-8");
        TUser user = userService.getUserInfo(name);
        if (user==null) {
            response.getWriter().write("nouser");
        }else if(!MD5.string2MD5(password).equals(user.getPassword())){
            response.getWriter().write("wrong");
        }else{
            response.getWriter().write(JSONObject.fromObject(user).toString());
            HttpSession session = request.getSession();
            String jsessionId = CookieUtil.getCookieValue(request.getCookies(), "JSESSIONID");
            session.setAttribute(jsessionId, user);
        }
        response.getWriter().flush();
    }

    /**
     * 登出
     * @param model
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/logout/", method=RequestMethod.POST)
    public void logout(Model model,HttpServletRequest request,HttpServletResponse response) throws IOException{
        request.getSession().setAttribute(request.getSession().getId(), null);
        System.out.println("userLogout"+request.getSession().getId());
        response.getWriter().write("");
        response.getWriter().flush();
    }

    /**
     * GET请求示例
     * @param model
     * @param request
     * @param response
     * @param id
     * @throws IOException
     */
    @RequestMapping(value="/getMethod/{id}/{type}", method=RequestMethod.GET)
    public void getUserSinglePostAddress(Model model,HttpServletRequest request,HttpServletResponse response,
                                         @PathVariable("id") String id,
                                         @PathVariable("type") String type) throws IOException{
        response.setCharacterEncoding("utf-8");
        PostInfo postInfo = userService.getUserSinglePostAddress(id);
        JSONObject postJson = JSONObject.fromObject(postInfo);
        response.getWriter().write(postJson.toString());
        response.getWriter().flush();
    }

    /**
     * jstl标签的使用
     * @param request
     * @param response
     * @param userId
     * @param orderID
     * @throws Exception
     */
    @RequestMapping(value = "/confirmed/{userId}/{orderID}/", method = RequestMethod.GET)
    public void confirmed(HttpServletRequest request,
                          HttpServletResponse response,
                          @PathVariable("userId") String userId,
                          @PathVariable("orderID") String orderID) throws Exception {
        log.info("查看订单详情");

        UserEntity user = userService.getUserInfoById(userId);// 获得用户信息
        // ShopEntity shopEntity =
        // orderService.getShopInfo(orderID);//根据ordeId查询 卖家信息
        List<OrderMsgBean> orderList = orderService.selectOrderDeatil(userId,
                orderID);// 根据userid和orderid查看订单详情
        UserEntity ShopOwnerInfo = orderService.getShopOwnerInfo(orderID);
        PostInfo postInfo = orderService.getPostInfo(orderID);// 根据orderid
        OrderMsgBean orderMsgBean = orderList.get(0);
        request.setAttribute("user", user);
        request.setAttribute("postInfo", postInfo);
        // request.setAttribute("shopEntity", shopEntity);
        request.setAttribute("orderMsgBean", orderMsgBean);
        String urlString = "receiving.jsp";
        request.getRequestDispatcher("../../../../" + urlString).forward(
                request, response);

    }

    @RequestMapping(value = "/purchaseNow/", method = RequestMethod.POST)
    public void purchaseNow(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam("userId") String userId,
                            @RequestParam("commodityId") String commodityId,
                            @RequestParam("mount") String mount,
                            @RequestParam("postId") String postId,
                            @RequestParam("remark") String remark) throws Exception {
        log.info("立即购买插入订单");
        String orderString = "";
        String [] strings= orderService.purchaseNow(userId, commodityId,postId,mount,remark);// 插入订单
        orderString = strings[0];
        String totalPrice = strings[1];
        String urlString = "pay.jsp?";
        request.setAttribute("totalPrice", totalPrice);
        request.setAttribute("ids", orderString);
        request.getRequestDispatcher("../../" + urlString).forward(request,
                response);

    }
}
