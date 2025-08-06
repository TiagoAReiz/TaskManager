package com.tiagoreiz.projeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Classe principal da aplicação TaskMaster.
 * Aplicação Spring Boot para gerenciamento de tarefas seguindo Clean Architecture.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@SpringBootApplication
@EnableTransactionManagement
public class TaskManagerApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot
     * 
     * @param args Argumentos da linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}