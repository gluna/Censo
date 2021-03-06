package censo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class ArquivoAlunos {
	
	Connection connection;
	Statement statement;
	SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");

	public static void main(String[] args) {
		new ArquivoAlunos();
	}
	
	public ArquivoAlunos() {
		
		conectarBanco();
		gerarArquivo();
	}
	
	
	private void gerarArquivo() {
	
		try {
			
			BufferedWriter buffWrite = new BufferedWriter(new FileWriter("alunos.txt"));
			
			String linha = "40|1027|4";
			buffWrite.append(linha + "\n");
			
			ResultSet rs = statement.executeQuery("select * " + 
					" from aluno, pessoa, curso, POLO, ALUNVINCULADOPOLO " + 
					" where aluno.cdpessoa = pessoa.cdpessoa " + 
					" and aluno.cdcurso = curso.cdcurso " + 
					" and aluno.cdaluno = ALUNVINCULADOPOLO.CDALUNO " + 
					" and aluno.CDCURSO = ALUNVINCULADOPOLO.CDCURSO " + 
					" and ALUNVINCULADOPOLO.CDPOLO = polo.CDPOLO " + 
					" and aluno.cdcurso in ('0000000024','0000000026','0000000029') " + 
					" and aluno.anoperiodoingresso <> '202001' " + 
					" and aluno.staluno = '01' ");
			rs.next();
			
			while(rs.next()) {
				
				linha = "41|"
					  + "|"
					  + rs.getString("NMPESSOA")+"|"
					  + rs.getString("CPF")+"|"
					  + getPassaporte(rs.getString("PASSAPORTE"))+"|"
					  + formato.format(rs.getDate("DTNASCIMENTO")) +"|"
					  + getSexo(rs.getString("SEXO"))+"|"
					  + "0|"
					  + rs.getString("NMMAE")+"|"
					  + getNacionalidade("CDNACIONALIDADE")+"|"
					  + "|"
					  + "|"
					  + "BRA|"
					  + "2|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "";
				
				buffWrite.append(linha + "\n");
				
				linha = "42|"
					  + "1|"
					  + rs.getString("CODIGOINEP")+"|" //codigo do curso
					  + getPolo(rs.getString("CODINEP"))+"|" //codigo do polo
					  + "|" //matricula do aluno
					  + "|" //turno
					  + getSituacaoAluno(rs.getString("STALUNO"))+"|" //situacao do aluno
					  + "|" // curso de origem
					  + getSemestreConclusao(rs.getString("CDALUNO"), rs.getString("STALUNO"))+"|" // semestre de conclusao
					  + "0|" //aluno parfor
					  + rs.getString("ANOPERIODOINGRESSO").substring(4)+rs.getString("ANOPERIODOINGRESSO").substring(0,4)+"|" //ano periodo ingresso
					  + "2|" //tipo escola segundo grau
					  + "1|0|0|0|0|0|0|0|0|0|" //forma de ingresso
					  + "0|" //mobilidade
					  + "|" //tipo mobilidade
					  + "|"
					  + "|"
					  + "|"
					  + "0|" //programa de reserva de vagas
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|" //financiamento estudantil
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|" //tipo financiamento
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "0|" //apoio social
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "0|" //atividade extra curricular
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + "|"
					  + rs.getString("CH")+"|"
					  + getCargaHoraria(rs.getString("CDALUNO"));
				
				buffWrite.append(linha + "\n");
			}
			
			buffWrite.close();
			
			System.out.println("KBÔ");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private String getPolo(String polo) {
		String p = "";
		
		if (polo != null) {
			p = polo;
		}
		return p;
	}

	private String getPassaporte(String passaporte) {
		String pass = "";
		if(passaporte != null) {
			pass = passaporte;
		}
		
		return pass;
	}

	private String getCargaHoraria(String cdaluno) {
		String ch = "0";
		
		try {
			
			ResultSet rs1 = connection.createStatement().executeQuery(" select sum(cast(disciplina.cargahoraria as integer)) as CH from matricula, disciplina"
												 +" where matricula.cddisciplina = disciplina.cddisciplina"
												 +" and matricula.stmatricula in ('02','06','08')"
												 +" and matricula.cdaluno = '"+cdaluno+"'");
			
			if(rs1.next()) {
				if(rs1.getString("CH") == null) {
				  ch = "0";
				}else {
					ch = rs1.getString("CH");
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ch;
	}

	private String getSemestreConclusao(String cdaluno, String staluno) {
		String conclusao = "";
		
		if(staluno.equals("09")) {
			try {
				ResultSet rs2 = connection.createStatement().executeQuery(" select * from conclusaocurso where cdaluno = '"+cdaluno+"'");
				
				if(rs2.next()) {
					if(!rs2.getString("ANOPERIODOCONCLUSAO").isEmpty()) {
						conclusao = rs2.getString("ANOPERIODOCONCLUSAO").substring(5);
					}
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return conclusao;
	}

	private String getSituacaoAluno(String situacao) {
		String st = "2";
		
		switch (situacao){
	        case "04":
	            st = "3";
	            break;
	        case "03":
	            st = "4";
	            break;
	        case "06":
	            st = "4";
	            break;
	        case "12":
	            st = "4";
	            break;
	        case "07":
	            st = "5";
	            break;
	        case "09":
	            st = "6";
	            break;
	        case "23":
	            st = "7";
	            break;
	            
		}    
		
		return st;
	}

	private String getUF(String estado) {
		String uf = "";
		
		switch (estado){
	        case "84":
	            uf = "11";
	            break;
	        case "81":
	            uf = "12";
	            break;
	        case "11":
	            uf = "13";
	            break;
	        case "85":
	            uf = "14";
	            break;
	        case "53":
	            uf = "15";
	            break;
	        case "83":
	            uf = "16";
	            break;
	        case "87":
	            uf = "17";
	            break;
	        case "80":
	            uf = "21";
	            break;
	        case "60":
	            uf = "22";
	            break;
	        case "20":
	            uf = "23";
	            break;
	        case "62":
	            uf = "24";
	            break;
	        case "55":
	            uf = "25";
	            break;
	        case "58":
	            uf = "26";
	            break;
	        case "82":
	            uf = "27";
	            break;
	        case "86":
	            uf = "28";
	            break;
	        case "13":
	            uf = "29";
	            break;
	        case "47":
	            uf = "31";
	            break;
	        case "25":
	            uf = "32";
	            break;
	        case "41":
	            uf = "33";
	            break;
	        case "69":
	            uf = "35";
	            break;
	        case "22":
	            uf = "41";
	            break;
	        case "65":
	            uf = "42";
	            break;
	        case "63":
	            uf = "43";
	            break;
	        case "46":
	            uf = "50";
	            break;
	        case "45":
	            uf = "51";
	            break;
	        case "40":
	            uf = "52";
	            break;
	        case "23":
	            uf = "53";
	            break;
		}
		
		return uf;
	}

	private String getNacionalidade(String nacionalidade) {
		String n = "1";
		
		if(nacionalidade.equals("002")) {
			n = "3";
		}else {
			if(nacionalidade.equals("003")) {
				n = "2";
			}
		}
		
		return n;
	}

	private String getSexo(String sexo) {
		String s = "0";
		
		if (sexo.equals("F")) {
			s = "1";
		}
		
		return s;
	}

	public void conectarBanco() {
		 try {

	         Class.forName("org.firebirdsql.jdbc.FBDriver");
	         connection = DriverManager.getConnection(
	               "jdbc:firebirdsql:localhost/3050:/home/gustavo/Sistemas/graduacao.gdb",
	               "sysdba",
	               "masterkey");
	         connection.setAutoCommit(false);
	         
	         statement = connection.createStatement();
	         
	         System.out.println("OK");
	      } catch (Exception e) {
	         System.out.println("N�o foi poss�vel conectar ao banco: " + e.getMessage());
	      }
	}

}
