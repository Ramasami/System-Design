package org.example.stack.overflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.stack.overflow.manager.StackOverFlow;
import org.example.stack.overflow.model.*;

import java.util.logging.Logger;

public class StackOverFlowApplication {
    private static final Logger logger = Logger.getLogger(StackOverFlowApplication.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public static void main(String[] args) {
        StackOverFlow stackOverFlow = StackOverFlow.getInstance();

        User johnDoe = stackOverFlow.createUser("john_doe");
        logger.info("Created user: " + mapper.writeValueAsString(johnDoe));

        User janeDoe = stackOverFlow.createUser("jane_doe");
        logger.info("Created user: " + mapper.writeValueAsString(janeDoe));

        User aliceSmith = stackOverFlow.createUser("alice_smith");
        logger.info("Created user: " + mapper.writeValueAsString(aliceSmith));

        Question question1 = stackOverFlow.createNewQuestion(johnDoe.getUserId(), "What is Java?", "I want to learn about Java programming language.");
        logger.info("Created question: " + mapper.writeValueAsString(question1));

        Tag tag1 = stackOverFlow.addTag(question1.getQuestionId(), "Java");
        logger.info("Added tag to question: " + mapper.writeValueAsString(tag1));

        Answer answer1 = stackOverFlow.addAnswerToQuestion(question1.getQuestionId(), "Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.", janeDoe.getUserId());
        logger.info("Added answer to question: " + mapper.writeValueAsString(answer1));

        Answer answer2 = stackOverFlow.addAnswerToQuestion(question1.getQuestionId(), "Java is a versatile language used for building applications across various platforms.", aliceSmith.getUserId());
        logger.info("Added answer to question: " + mapper.writeValueAsString(answer2));

        Comment comment1 = stackOverFlow.addCommentToAnswer(answer1.getAnswerId(), "Great explanation!", johnDoe.getUserId());
        logger.info("Added comment to answer: " + mapper.writeValueAsString(comment1));

        Comment comment2 = stackOverFlow.addCommentToAnswer(answer2.getAnswerId(), "I agree, Java is very versatile.", janeDoe.getUserId());
        logger.info("Added comment to answer: " + mapper.writeValueAsString(comment2));

        Comment comment3 = stackOverFlow.addCommentToAnswer(answer2.getAnswerId(), "Thanks for the insights!", aliceSmith.getUserId());
        logger.info("Added comment to answer: " + mapper.writeValueAsString(comment3));

        stackOverFlow.addVoteToQuestion(question1.getQuestionId(), janeDoe.getUserId(), VoteType.UP);
        logger.info("Added vote to question: " + mapper.writeValueAsString(question1.getQuestionId()) + " by user: " + mapper.writeValueAsString(janeDoe.getUserId()));

        stackOverFlow.addVoteToAnswer(answer1.getAnswerId(), johnDoe.getUserId(), VoteType.UP);
        logger.info("Added vote to answer: " + mapper.writeValueAsString(answer1.getAnswerId()) + " by user: " + mapper.writeValueAsString(johnDoe.getUserId()));

        int answerVotes = stackOverFlow.getVoteCountForAnswer(answer2.getAnswerId());
        logger.info("Vote count for answer " + mapper.writeValueAsString(answer2.getAnswerId()) + ": " + answerVotes);

        int questionVotes = stackOverFlow.getVoteCountForQuestion(question1.getQuestionId());
        logger.info("Vote count for question " + mapper.writeValueAsString(question1.getQuestionId()) + ": " + questionVotes);

        

        logger.info("Post: " + mapper.writeValueAsString(question1));
        logger.info("John Doe: " + mapper.writeValueAsString(johnDoe));
        logger.info("Jane Doe: " + mapper.writeValueAsString(janeDoe));
        logger.info("Alice Smith: " + mapper.writeValueAsString(aliceSmith));

        stackOverFlow.removeVoteFromQuestion(question1.getQuestionId(), janeDoe.getUserId());
        logger.info("John Doe: " + mapper.writeValueAsString(johnDoe));
    }
}
