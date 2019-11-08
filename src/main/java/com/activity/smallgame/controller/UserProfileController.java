package com.activity.smallgame.controller;

import com.activity.smallgame.constant.MailConstant;
import com.activity.smallgame.dto.ResultDto;
import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.exception.SmallGameException;
import com.activity.smallgame.model.mongodb.UserProfile;
import com.activity.smallgame.request.AuthenticationRequest;
import com.activity.smallgame.request.UserProfileRequest;
import com.activity.smallgame.service.SendMailService;
import com.activity.smallgame.service.UserProfileService;
import com.activity.smallgame.service.UserWalletService;
import com.activity.smallgame.utils.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Mr.dong
 * @create: 2019-08-21 16:51
 **/
@RestController
public class UserProfileController {

    private Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private SendMailService sendMailService;

    @ApiOperation(value = "增加用户详情")
    @PostMapping("/api/v1/user/profile")
    public ResultDto addUserProfile(@RequestBody UserProfileRequest userProfileRequest) {
        try {
            userProfileService.addUserProfile(userProfileRequest.getName(), userProfileRequest.getMobile(), userProfileRequest.getEmail(), userProfileRequest.getAuthCode());
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("UserProfileController addUserProfile error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
        return ResponseUtil.success();
    }

    @ApiOperation(value = "增加用户钱包")
    @PostMapping("/api/v1/user/wallet")
    public ResultDto addUserWallet(@RequestParam("userCode")String userCode) {
        try {
            UserProfile userProfile = userProfileService.getUserProfileByUserCode(userCode);
            if (userProfile != null) {
                userWalletService.addUserWallet(userCode);
            }
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("UserProfileController addUserWallet error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
        return ResponseUtil.success();
    }

    @ApiOperation(value = "获取用户详情")
    @GetMapping("/api/v1/user/profile/{userCode}")
    public ResultDto getUserProfile(@PathVariable("userCode")String userCode) {
        try {
            UserProfile userProfile = userProfileService.getUserProfileByUserCode(userCode);
            return ResponseUtil.success(userProfile);
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("UserProfileController userUseMiningArea error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
    }

    @ApiOperation(value = "发送邮件")
    @PostMapping("/api/v1/send/email")
    public ResultDto sendEmail(@RequestBody UserProfileRequest userProfileRequest) {
        try {
            String content = String.format(MailConstant.MAIL_CONTENT_FROM, userProfileRequest.getName(), userProfileRequest.getMobile(), userProfileRequest.getEmail(), userProfileRequest.getGoal());
            sendMailService.sendSimpleMail(userProfileRequest.getEmail(), MailConstant.MAIL_SUBJECT_FROM, content, userProfileRequest.getMobile(), false);
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("UserProfileController sendEmail error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
        return ResponseUtil.success();
    }

    @ApiOperation(value = "用户授权")
    @PostMapping("/api/v1/user/auth")
    public ResultDto authentication(@RequestBody AuthenticationRequest request) {
        try {
            UserProfile userProfile = userProfileService.getUserProfileByMobileAndUserCode(request.getMobile(), request.getUserCode());
            if (null == userProfile) {
                return ResponseUtil.error(ResultEnum.FAILED.getCode(), ResultEnum.FAILED.getMsg());
            }
            return ResponseUtil.success(userProfile);
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("UserProfileController authentication error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
    }
}
