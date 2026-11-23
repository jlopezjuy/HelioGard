import { Component, computed, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { visibleInstallations } from '../../core/domain/access';
import { DEMO_REPOSITORY } from '../../infra/mock/demo-store';
import { readyPhase } from '../../shared/load';

@Component({
  selector: 'app-audits',
  imports: [ReactiveFormsModule],
  templateUrl: './audits.html',
})
export class AuditsPage {
  private readonly store = inject(DEMO_REPOSITORY);
  protected readonly phase = readyPhase();
  protected readonly form = computed(() => {
    const report = this.store.snapshot().auditReports.find((item) => item.id === 'audit-puna');
    const controls: Record<string, FormControl<string>> = {};
    for (const answer of this.store.snapshot().auditAnswers.filter((item) => item.auditReportId === report?.id)) {
      controls[answer.id] = new FormControl(answer.answerText, { nonNullable: true });
    }
    return new FormGroup(controls);
  });
  protected readonly view = computed(() => {
    const snapshot = this.store.snapshot();
    const ids = new Set(visibleInstallations(snapshot).map((item) => item.id));
    const report = snapshot.auditReports.find((item) => ids.has(item.installationId)) ?? null;
    const questions = snapshot.auditQuestions.filter((item) => item.auditTemplateId === report?.auditTemplateId);
    const answers = snapshot.auditAnswers.filter((item) => item.auditReportId === report?.id);
    const findings = snapshot.auditFindings.filter((item) => item.auditReportId === report?.id);
    const template = snapshot.auditTemplates.find((item) => item.id === report?.auditTemplateId) ?? null;
    return { report, questions, answers, findings, template, evidence: snapshot.auditEvidence };
  });

  protected answerId(answers: { id: string; auditQuestionId: string }[], questionId: string): string {
    return answers.find((answer) => answer.auditQuestionId === questionId)?.id ?? '';
  }

  protected save(status: 'IN_PROGRESS' | 'SUBMITTED'): void {
    const raw = this.form().getRawValue();
    this.store.saveAudit(raw, status);
  }

  protected reopen(): void {
    this.store.reopenAudit();
  }
}
