package com.examguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "question_options")
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 5)
    private String optionLetter;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String optionText;

    @Column(nullable = false)
    private boolean isCorrect = false;

    public QuestionOption() {}

    public QuestionOption(String optionLetter, String optionText, boolean isCorrect) {
        this.optionLetter = optionLetter;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public String getOptionLetter() { return optionLetter; }
    public void setOptionLetter(String optionLetter) { this.optionLetter = optionLetter; }
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}

