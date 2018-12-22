package com.store.service;

import com.store.pojo.TbSeller;
import com.store.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("经过了spring-security的验证");
        List<GrantedAuthority> grantAuths = new ArrayList();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        /**
         * User:
         * * 参数：
         * 	* 1.用户名
         *  * 2.密码
         *  * 3.认证信息（角色）
         */
        // 去数据库进行查询:
        TbSeller seller = sellerService.findOne(username);
        if (seller != null) {
            if ("1".equals(seller.getStatus())) {
                return new User(username, seller.getPassword(), grantAuths);
            } else {
                return null;
            }
        }
        return null;

    }

}
