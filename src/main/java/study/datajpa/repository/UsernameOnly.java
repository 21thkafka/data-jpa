package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    //open projection
    //@Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
