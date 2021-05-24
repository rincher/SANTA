package sparta.enby.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import sparta.enby.model.Board;
import sparta.enby.repository.BoardRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Aspect
@RequiredArgsConstructor
public class DeadlineAop {
    private final BoardRepository boardRepository;

    @Around("execution(public * sparta.enby.controller..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
        LocalDateTime now = LocalDateTime.now();
        try {
            return joinPoint.proceed();
        }finally {
            List<Board> boards = boardRepository.findAll();
            for (Board board : boards){
                LocalDateTime meetTime = board.getMeetTime();
                if (meetTime.compareTo(now)<0){
                    board.changeDeadlineStatus(true);
                    boardRepository.save(board);
                }
            }
        }
    }
}
