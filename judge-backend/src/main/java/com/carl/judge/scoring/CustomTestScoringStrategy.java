package com.carl.judge.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.carl.judge.model.dto.question.QuestionContentDTO;
import com.carl.judge.model.entity.App;
import com.carl.judge.model.entity.Question;
import com.carl.judge.model.entity.ScoringResult;
import com.carl.judge.model.entity.UserAnswer;
import com.carl.judge.model.vo.QuestionVO;
import com.carl.judge.service.AppService;
import com.carl.judge.service.QuestionService;
import com.carl.judge.service.ScoringResultService;
import io.swagger.models.auth.In;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ScoringStrategyConfig(appType = 1, scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        // 根据id查询到题目和题目结果信息
        Long id = app.getId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, id)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, id)
        );
        //统计用户每个选择对应的属性个数，如I = 10个
        Map<String , Integer> optionCount = new HashMap<>();
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentDTOS = questionVO.getQuestionContent();
        for(QuestionContentDTO questionContentDTO : questionContentDTOS){
            for (String answer : choices){
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()){
                    if(option.getKey().equals(answer)){
                        String result = option.getResult();
                        if (!optionCount.containsKey(result)){
                            optionCount.put(result, 1);
                        }
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);
        //遍历每种评分结果，计算哪个结果的得分更高
        for (ScoringResult scoringResult : scoringResultList){
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            int score = resultProp.stream().mapToInt(prop -> optionCount.getOrDefault(prop, 0)).sum();

            if(score > maxScore){
                maxScore = score;
                maxScoringResult = scoringResult;
            }
        }


        //构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());

        return userAnswer;
    }
}
