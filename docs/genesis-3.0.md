Temi
====

Argomenti di interesse per una nuova applicazione.


Comportamento emergente
------------------------

Il comportamento emergente è una caratteristica di un sistema complesso non formalizzata e imprevedibile
che "emerge" come comportamente autonomo derivato da un numero elevato di interazioni espresse da regole semplici.

Regole
------

Normalmente lo stato del sistema è definito dai valori di certe proprietà in un determinato istante (spazio delle fasi)
`S: (S1(t), ..., Sn(t))`.
Se nella rappresentazione dello stato aggiungiamo anche il tempo avremo che le transizioni di stato possono spostare il tempo in ogni direzione e di quantità variabili.

Le regole allora si potrebbero rappresentatre come `R: S(S1, ..., Sn, t) -> S'(S1', ..., Sn' ,t')`

Dinamica non lineare
-------------------

Il sistema viene normalmente descritto da equazioni differenziali non lineari che possono generare comportamenti o evoluzioni caotiche, dove condizioni iniziali molto simili possono portare a risultati completamente dissimili, creando
di fatto un alto grado di incertezza nella previsione del sistema causato dall'imprecisione delle misure.

Possiamo modellare il sistema con componenti interconnessi attraversio funzioni differenziali parametriche.
I parametri possono essere controllati dal giocatore e rappresentano i punti di controllo o decisionali.
Il fatto che siano funzioni differenziali permettono in qualche modo di introdurre effetti di anticipo o ritardo
tra causa ed effetto.


Esempi
------

Esempi di sistemi non lineari posso essere modelli di eco sistemi biologici, metereologici, finanziari, economici, fisica, fanta-fisica (sistemi fisici con regole di fantasia). 


Simulazione con automi 2D/3D
----------------------------

Simulare il comportamento del sistea con automi su uno spazione 2D o 3D.


LWJGL
-----

Implementazione della UI con un layer grafico comune per desktop (LWJGL) / Android (OpenGL).


Interazioni
-----------

Il giocatore può esplorare lo spazio 3D spostandosi e modificandolo.
Può inserire, togliere o cambiare i blocchi.

Esistono vari tipi di blocchi, ogni blocco ha diverse proprietà:

* solido: il blocco non può essere attraversato,
* liquido/gassoso: il blocco può essere attraversato,


Risoluzione delle collisioni
----------------------------

Precondizione l'oggetto non era in conflitto nell'istante precedente. 
Si parte ad una lista di volumi di collisione.
Per ogni volume si trovano le azioni possibili per risolvere la collisione (modifica dei valori di x, y, z
riportandoli ai valori precedenti il movimento e anullando la velocità nella relativa direzione).
Congettura: è sufficiente una sola azione in una determinata direzione per risolvere il conflitto su un blocco.
Si calcola quindi il numero minimo di operazioni che risolvono tutti i conflitti.
Il numero massimo di operazioni sono 3 pari ai gradi di liberà del personaggio (si annulla completamente l'effetto
del movimento bloccando l'oggetto).


Test
----

(-1,0,9)    (0,0,9)

(-1,-1,10)  (0,0,10)

    
     -0.9 -0.6 -0.3 +0.0 +0.3 +0.6 +0.9
 5.1   +---------+---------+---------+
       |         |         |         |
 5.4   |-1, 0, 9 | 0, 0, 9 | 1, 0, 9 |
       |         |         |         |
 5.7   +---------+---------+---------+
       |         |         |         |
 6.0   |-1,-1,10 | 0, 0,10 | 1, 0,10 |
       |         |         |         |
 6.3   +---------+---------+---------+
       |         |         |         |
 6.6   |-1,-1,11 | 0,-1,11 | 1,-1,11 |
       |         |         |         |
 6.9   +---------+---------+---------+

