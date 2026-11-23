import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

@Component({
  selector: 'app-denied',
  imports: [RouterLink],
  templateUrl: './denied.html',
})
export class Denied {
  private readonly route = inject(ActivatedRoute);
  protected readonly from = toSignal(this.route.queryParamMap.pipe(map((params) => params.get('desde'))), {
    initialValue: null,
  });
}
