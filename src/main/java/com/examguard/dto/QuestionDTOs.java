package com.examguard.dto;

import com.examguard.entity.enums.Enums.DifficultyLevel;
import com.examguard.entity.enums.Enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class QuestionDTOs {

    public static class QuestionCreateDTO {
        @NotNull private Long subjectId;
        @NotBlank private String questionText;
        private QuestionType questionType = QuestionType.MCQ;
        private BigDecimal marks = BigDecimal.valueOf(1.00);
        private BigDecimal negativeMarks = BigDecimal.ZERO;
        private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;
        private Integer unitNumber = 1;
        private String explanation;
        private String imageUrl;
        private List<QuestionOptionDTO> options;

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public QuestionType getQuestionType() { return questionType; }
        public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
        public BigDecimal getMarks() { return marks; }
        public void setMarks(BigDecimal marks) { this.marks = marks; }
        public BigDecimal getNegativeMarks() { return negativeMarks; }
        public void setNegativeMarks(BigDecimal negativeMarks) { this.negativeMarks = negativeMarks; }
        public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
        public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }
        public Integer getUnitNumber() { return unitNumber; }
        public void setUnitNumber(Integer unitNumber) { this.unitNumber = unitNumber; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public List<QuestionOptionDTO> getOptions() { return options; }
        public void setOptions(List<QuestionOptionDTO> options) { this.options = options; }
    }

    public static class QuestionOptionDTO {
        private Long id;
        private String optionLetter;
        private String optionText;
        private boolean isCorrect;

        public QuestionOptionDTO() {}
        public QuestionOptionDTO(String optionLetter, String optionText, boolean isCorrect) {
            this.optionLetter = optionLetter;
            this.optionText = optionText;
            this.isCorrect = isCorrect;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOptionLetter() { return optionLetter; }
        public void setOptionLetter(String optionLetter) { this.optionLetter = optionLetter; }
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
    }

    public static class QuestionResponseDTO {
        private Long id;
        private Long subjectId;
        private String subjectName;
        private String questionText;
        private QuestionType questionType;
        private BigDecimal marks;
        private BigDecimal negativeMarks;
        private DifficultyLevel difficultyLevel;
        private Integer unitNumber;
        private String explanation;
        private String imageUrl;
        private List<QuestionOptionDTO> options;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public QuestionType getQuestionType() { return questionType; }
        public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
        public BigDecimal getMarks() { return marks; }
        public void setMarks(BigDecimal marks) { this.marks = marks; }
        public BigDecimal getNegativeMarks() { return negativeMarks; }
        public void setNegativeMarks(BigDecimal negativeMarks) { this.negativeMarks = negativeMarks; }
        public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
        public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }
        public Integer getUnitNumber() { return unitNumber; }
        public void setUnitNumber(Integer unitNumber) { this.unitNumber = unitNumber; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public List<QuestionOptionDTO> getOptions() { return options; }
        public void setOptions(List<QuestionOptionDTO> options) { this.options = options; }
    }
}

