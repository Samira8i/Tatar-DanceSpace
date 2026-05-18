package com.tatardancespace.exception;

public class AlreadyReviewedException extends BusinessException {
    public AlreadyReviewedException() {
        super("Вы уже оставляли отзыв на этот зал");
    }
}