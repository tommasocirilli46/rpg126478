# Dichiarazione sull'uso dell'IA

> Questa pagina dichiara in modo trasparente come sono stati impiegati strumenti di
> intelligenza artificiale durante lo sviluppo del progetto. È redatta in forma di modello:
> verifica che ogni punto corrisponda a quanto hai effettivamente fatto e personalizza le
> parti tra parentesi quadre.

## Strumenti utilizzati

Durante lo sviluppo è stato utilizzato un assistente conversazionale basato su modello
linguistico di grandi dimensioni (LLM) come supporto alla progettazione, alla scrittura
del codice e alla stesura della documentazione.

## In che cosa l'IA ha fornito supporto

- **Progettazione dell'architettura:** definizione della suddivisione in livelli
  (modello, motore, persistenza, interfaccia) e applicazione dei principi SOLID, tra cui
  la scelta del pattern Repository per la persistenza e del pattern Observer per
  disaccoppiare motore e interfaccia.
- **Generazione del codice:** stesura iniziale delle classi di dominio, del motore di
  gioco, delle implementazioni JSON dei repository, dell'interfaccia JavaFX e dei test
  unitari.
- **Contenuti e configurazione:** redazione della storia di esempio in formato JSON, del
  file di build Gradle e del tema grafico.
- **Documentazione:** prima stesura del `README` e delle pagine di questa Wiki.

## Come è stato verificato e fatto proprio il lavoro

- [Ho letto e compreso ogni classe del progetto, riformulando i commenti dove necessario.]
- [Ho compilato ed eseguito il progetto in locale verificando il corretto funzionamento di
  nuova partita, scelte con requisiti, effetti, finali, salvataggio e caricamento.]
- [Ho eseguito i test con `./gradlew test` e ne ho verificato l'esito.]
- [Ho modificato/aggiunto personalmente: descrivere qui le parti scritte o cambiate in
  autonomia, ad esempio una nuova scena, un nuovo tipo di effetto, una modifica
  all'interfaccia.]
- [Sono in grado di spiegare a voce qualsiasi parte del codice e le scelte progettuali.]

## Responsabilità

Il sottoscritto [Nome Cognome, matricola] dichiara di aver verificato la correttezza del
codice e dei contenuti generati con il supporto dell'IA, di averne compreso il
funzionamento e di assumersene la piena responsabilità ai fini della valutazione del
corso di Metodologie di Programmazione (A.A. 2025/26).
