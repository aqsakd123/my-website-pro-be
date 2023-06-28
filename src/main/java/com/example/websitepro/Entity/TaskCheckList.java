package com.example.websitepro.Entity;

import com.example.websitepro.Config.Constant;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "TASK_CHECK_LIST")
@EntityListeners(AuditingEntityListener.class)
public class TaskCheckList implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "START_DATE")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "LOOP_TIME_LINE")
//    @Temporal(TemporalType.TIME)
//    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalDateTime loopTime;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    @Column(name = "MODIFY_DATE")
    private Date modifyDate;

    @Column(name = "MODIFIED_BY")
    @LastModifiedBy
    private String modifiedBy;

    @Column(name = "PRIORITY")
    private Long priority = 1L;

    @Column(name = "TASK_LIST_ORDER")
    private Double taskListOrder;

    @Column(name = "TYPE_CODE")
    private String typeCode;

    @Column(name = "TYPE_GROUP")
    private Long typeGroup = 1L;

    @Column(name = "TOTAL_CREDIT")
    private Long totalCredit = 0L;

    @Column(name = "IS_CREDITED")
    private Long isCredited = 0L;

    @Column(name = "ROUTINE_DATE")
    private Long routineDate = 1L;

    @Column(name = "TYPE_ROUTINE")
    private String routineType;

    @Column(name = "PINNED")
    private Boolean pinned = false;

    @Column(name="IS_COMPLETED")
    private Boolean isCompleted = false;

    @Column(name="IS_DELETED")
    private Boolean isDeleted = false;

    @Column(name = "PROJECT_PARENT_ID")
    private Long projectParentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", updatable = false)
    private TaskCheckList parent;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    @OrderBy("pinned desc, taskListOrder ASC")
    @Where(clause = "IS_DELETED = false")
    @JoinColumn(name = "PARENT_ID")
    private List<TaskCheckList> children;

    @Column(name = "routine")
    @ElementCollection
    @JoinTable(name = "task_routine", joinColumns = @JoinColumn(name = "id"))
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<String> routine;

    public List<String> getRoutine(){
        if((Constant.TASK_TYPE_CODE.DAILY).equals(typeCode)){
            return routine;
        } else {
            return Collections.emptyList();
        }
    }

    public void setRoutine(List<String> routine) {
        if((Constant.TASK_TYPE_CODE.DAILY).equals(typeCode)){
            this.routine = routine;
        }
    }
}
