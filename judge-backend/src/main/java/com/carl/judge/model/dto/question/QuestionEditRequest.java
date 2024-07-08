package com.carl.judge.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑题目请求
 *
 * 
 * 
 */
@Data
public class QuestionEditRequest implements Serializable {

    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentDTO> questionContent;


    private static final long serialVersionUID = 1L;
}