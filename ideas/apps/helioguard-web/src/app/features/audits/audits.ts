import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DemoStore } from '../../infra/mock/demo-store';

@Component({
  selector: 'app-audits',
  imports: [FormsModule],
  templateUrl: './audits.html',
})
export class Audits {
  protected readonly store = inject(DemoStore);
  protected readonly note = signal('');
  protected readonly report = computed(() =>
    this.store.state().auditReports.find((item) => item.organizationId === this.store.state().activeOrganizationId) ?? null,
  );
  protected questions(templateId: string) {
    return this.store.state().auditQuestions.filter((item) => item.auditTemplateId === templateId).sort((a, b) => a.sortOrder - b.sortOrder);
  }
  protected answer(questionId: string) {
    const report = this.report();
    return this.store.state().auditAnswers.find((item) => item.auditReportId === report?.id && item.auditQuestionId === questionId) ?? null;
  }
  protected findings() {
    const report = this.report();
    return this.store.state().auditFindings.filter((item) => item.auditReportId === report?.id);
  }
  protected save(questionId: string, compliant: boolean | null): void {
    const answer = this.answer(questionId);
    if (!answer) {
      return;
    }
    this.store.saveAnswer(answer.id, compliant, this.note());
  }
}
