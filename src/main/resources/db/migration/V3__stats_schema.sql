CREATE TABLE Stats_Transportista (
  id INTEGER PRIMARY KEY,
  transportista_id INTEGER,
  total_ordenes INTEGER DEFAULT 0,
  largas INTEGER DEFAULT 0,
  largas_exitosas INTEGER DEFAULT 0,
  medias INTEGER DEFAULT 0,
  medias_exitosas INTEGER DEFAULT 0,
  cortas INTEGER DEFAULT 0,
  cortas_exitosas INTEGER DEFAULT 0,
  pesadas INTEGER DEFAULT 0,
  pesadas_exitosas INTEGER DEFAULT 0,
  livianas INTEGER DEFAULT 0,
  livianas_exitosas INTEGER DEFAULT 0,
  incidencias_graves INTEGER DEFAULT 0,
  FOREIGN KEY (transportista_id) REFERENCES Transportista(id)
);

CREATE TABLE Stats_System (
  total_ordenes INTEGER DEFAULT 0,
  largas INTEGER DEFAULT 0,
  largas_exitosas INTEGER DEFAULT 0,
  medias INTEGER DEFAULT 0,
  medias_exitosas INTEGER DEFAULT 0,
  cortas INTEGER DEFAULT 0,
  cortas_exitosas INTEGER DEFAULT 0,
  pesadas INTEGER DEFAULT 0,
  pesadas_exitosas INTEGER DEFAULT 0,
  livianas INTEGER DEFAULT 0,
  livianas_exitosas INTEGER DEFAULT 0
);