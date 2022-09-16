package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManagerService;
import com.yuhao.VO.MovementsVo;
import com.yuhao.VO.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/manage")
public class ManagerController {


    @Autowired
    ManagerService managerService;

    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = managerService.findAllUsers(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据id查询用户详情
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity findById(@PathVariable("userId") Long userId) {
        return managerService.findById(userId);
    }
    /**
     * 查询指定用户发布的所有视频列表
     */
    @GetMapping("/videos")
    public ResponseEntity videos(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pagesize,
                                 Long uid ) {
        PageResult pageResult = managerService.getVideosList(page, pagesize, uid);
        return ResponseEntity.ok(pageResult);
    }

    //查看用户动态
    @GetMapping("/messages")
    public ResponseEntity messages(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   Long uid,String state ) {
        System.out.println("begin");
        // 经检验  state 居然会传来 "''"这种数据 不是空 而是两个单引号 所以不能是Integer 而是string 且要额外判断空
        Integer state1 = null;
        if (!StringUtils.isEmpty(state) && !state.equals("''")){
            state1 = Integer.valueOf(state);
        }
        PageResult result = managerService.findAllMovements(page,pagesize,uid,state1);
        return ResponseEntity.ok(result);
    }
    ///manage/messages/:id
    @GetMapping("/messages/{id}")
    public ResponseEntity momentDetail(@PathVariable String id){
        MovementsVo movementsVo = managerService.getMomentDetail(id);
        return ResponseEntity.ok(movementsVo);
    }
    //用户冻结
    @PostMapping("/users/freeze")
    public ResponseEntity freeze(@RequestBody Map params) {
        Map map =  managerService.userFreeze(params);
        return ResponseEntity.ok(map);
    }

    //用户解冻
    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody  Map params) {
        Map map =  managerService.userUnfreeze(params);
        return ResponseEntity.ok(map);
    }
}
