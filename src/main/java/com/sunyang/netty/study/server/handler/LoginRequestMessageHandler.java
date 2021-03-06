package com.sunyang.netty.study.server.handler;

import com.sunyang.netty.study.message.LoginRequestMessage;
import com.sunyang.netty.study.message.LoginResponseMessage;
import com.sunyang.netty.study.server.service.UserServiceFactory;
import com.sunyang.netty.study.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: netty-study
 * @description:
 * @author: SunYang
 * @create: 2021-08-23 20:19
 **/
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        LoginResponseMessage message;
        boolean login = UserServiceFactory.getUserService().login(username, password);
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "登陆成功");
        } else {
            message = new LoginResponseMessage(false, "用户名或密码不正确");
        }
        ctx.writeAndFlush(message);
    }
}
