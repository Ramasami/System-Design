package org.example.stack.overflow.manager;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.*;
import org.example.stack.overflow.service.*;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StackOverFlow {

    private static StackOverFlow instance;

    public static StackOverFlow getInstance() {
        if (instance == null) {
            synchronized (StackOverFlow.class) {
                if (instance == null) {
                    instance = new StackOverFlow();
                }
            }
        }
        return instance;
    }

    public User createUser(String username) {
        return UserService.getInstance().createUser(username);
    }

    public Question createNewQuestion(int author, String title, String content) {
        return QuestionService.getInstance().createNewQuestion(author, title, content);
    }

    public Answer addAnswerToQuestion(int questionId, String content, int author) {
        return AnswerService.getInstance().addAnswer(questionId, content, author);
    }

    public Tag addTag(int questionId, String tagName) {
        return TagService.getInstance().addTag(questionId, tagName);
    }

    public Comment addCommentToAnswer(int answerId, String content, int authorId) {
        return CommentService.getInstance().addComment(answerId, content, authorId);
    }

    public void addVoteToQuestion(int questionId, int userId, VoteType voteType) {
        VoteService.getInstance().addVoteToQuestion(questionId, userId, voteType);
    }

    public void addVoteToAnswer(int questionId, int userId, VoteType voteType) {
        VoteService.getInstance().addVoteToAnswer(questionId, userId, voteType);
    }

    public int getVoteCountForAnswer(int answerId) {
        return VoteService.getInstance().getVoteCountForAnswer(answerId);
    }

    public int getVoteCountForQuestion(int questionId) {
        return VoteService.getInstance().getVoteCountForQuestion(questionId);
    }

    public boolean removeVoteFromQuestion(int questionId, int userId) {
        return VoteService.getInstance().removeVoteFromQuestion(questionId, userId);
    }

    public boolean removeVoteFromAnswer(int answerId, int userId) {
        return VoteService.getInstance().removeVoteFromAnswer(answerId, userId);
    }

    public int getUserReputation(int userId) {
        return UserService.getInstance().getReputation(userId);
    }

    public boolean acceptAnswer(int questionId, int answerId, int authorId) {
        return AnswerService.getInstance().acceptAnswer(questionId, answerId, authorId);
    }
}
