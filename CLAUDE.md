# GymCore API

Back-end Spring Boot do GymCore (sistema multitenant para academias/estúdios).
Front-end correspondente: `../gymcore-web` (Angular).

## Documentação de referência (`docs/`)

- `status-implementacao.md`: o que está pronto e a ordem de desenvolvimento.
- `documentacao-requisitos-sistema.md` / `documentacao-modelagem-banco.md`: requisitos e modelo de dados.
- `regras-inativacao-entidades.md`: dependências que bloqueiam/cascateiam a inativação de cada entidade.
- `padrao-ux-inativacao.md`: **padrão obrigatório** de inativação (fluxo de UX + contrato de API).

## Convenções

- **Inativação / deleção lógica**: toda nova inativação ou alteração em uma
  existente DEVE seguir `docs/padrao-ux-inativacao.md` (endpoint de verificação,
  pendências `BLOQUEANTE`/`CASCATA`, `409` com a lista de pendências, cascata
  transacional, filtro por tenant). Ler o documento antes de implementar.
- Docs compartilhados com `gymcore-web/docs` devem permanecer idênticos nos dois repositórios.
