package com.cricket.details.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity // cannot be a record
@Table(name = "score")
public class Score {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private int runs;

        private String result;

        private String match;

        @ManyToOne(fetch = FetchType.LAZY)
        private User user;

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public int getRuns() {
                return runs;
        }

        public void setRuns(int runs) {
                this.runs = runs;
        }

        public String getResult() {
                return result;
        }

        public void setResult(String result) {
                this.result = result;
        }

        public String getMatch() {
                return match;
        }

        public void setMatch(String match) {
                this.match = match;
        }

        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        // must for JPA else wil get error as org.hibernate.InstantiationException: No
        // default constructor for entity
        protected Score() {

        }

        public Score(Long id, int runs, String result, String match, User user) {
                this.id = id;
                this.runs = runs;
                this.result = result;
                this.match = match;
                this.user = user;
        }

}