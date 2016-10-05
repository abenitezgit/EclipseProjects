package socketServer;

public class ClientRequest {
	
	private String query = null;			// nombre del SP a ejecutar.
	private String parameters = null;		// listado de los parametros separados por | (pipe)
	private String servicio = null;			// Identificador de la Conexion: CONEXION1
	private int select = 0;					// Tipo de instruccion SQL: select / update
	private String requestID = null;		// Identificador único para el request.	
	private String outParameters = null;	// listado de Parametros de Salida (para SP, PL SQL) separados por | (pipe)
	private String dataTypes = null;		// listado de Tipos de Datos separados por | (pipe) y van relacionados con "parameters"
											// Valores Validos int, float, string, clob, cursor, (En Oracle con Valores Nulos para el Tipo de Dato, se envia como parametro de entrada colocar "null")
	private String connectionType = null;   //DB:Database REST: URL API REST
	private String restType = null;			//Tipo de Ejecucion: GET, POST, PUT
	private String restResource = null;     //recurso a consumir
	
	
	
	// { "key" : "valor de key" , "value" : "valor del atributo value" ... }
	public ClientRequest() {}

	public String getRestResource() {
		return restResource;
	}

	public void setRestResource(String restResource) {
		this.restResource = restResource;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public String getRestType() {
		return restType;
	}

	public void setRestType(String restType) {
		this.restType = restType;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public int getSelect() {
		return select;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	public String getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(String dataTypes) {
		this.dataTypes = dataTypes;
	}

	public String getOutParameters() {
		return outParameters;
	}

	public void setOutParameters(String outParameters) {
		this.outParameters = outParameters;
	}

}
