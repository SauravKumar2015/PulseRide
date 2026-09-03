package com.pulseride.admin.service;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
class AdminServiceTest { @Test void sensitiveChangesAreAudited() { var s=new AdminService(); s.status("a1","USER","u1","SUSPENDED"); assertThat(s.audit()).hasSize(1); } }
