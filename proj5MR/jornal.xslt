<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body>
				<h2>Jornal</h2>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="noticia">
		<p>
			<xsl:apply-templates select="titulo" />
			<xsl:apply-templates select="author" />
			<xsl:apply-templates select="date" />
			<xsl:apply-templates select="highlights" />
			<xsl:apply-templates select="newstext" />
		</p>
	</xsl:template>

	<xsl:template match="titulo">
		Titulo:
		<span style="color:#ff0000">
			<xsl:value-of select="." />
		</span>
		<br />
	</xsl:template>

	<xsl:template match="date">
		Data:
		<span style="color:#00ff00">
			<xsl:value-of select="." />
		</span>
		<br />
	</xsl:template>

	<xsl:template match="author">
		Autor:
		<span style="color:#00ff00">
			<xsl:value-of select="." />
		</span>
		<br />
	</xsl:template>

	<xsl:template match="highlights">
		Highlights:
		<span style="color:#00ff00">
			<xsl:value-of select="." />
		</span>
		<br />
	</xsl:template>

	<xsl:template match="newstext">
		Conteudo da noticia:
		<span style="color:#00ff00">
			<xsl:value-of select="." />
		</span>
		<br />
	</xsl:template>

</xsl:stylesheet>