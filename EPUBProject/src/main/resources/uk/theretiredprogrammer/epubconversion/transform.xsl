<?xml version="1.0"?>

<!--
    Document   : transform.xsl
    Created on : 13 January 2023, 16:18
    Author     : richard linsdale
    Description:
        conversion of epub xhtml, modified by hints to final html
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    
   
    <xsl:template match="table">
        <table>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="style">
                <xsl:value-of select="concat('border-collapse: collapse;',@style)"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
   
    <xsl:template match="td">
        <td>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="style">
                <xsl:value-of select="concat('border: 1px solid #000000; vertical-align: middle;',@style)"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </td>
    </xsl:template>
    
    <xsl:template match="p[span[contains(@style,'heading:')]]" >
        <xsl:choose>
            <xsl:when test="span[contains(@style,'heading: h3')]">
                <h3>
                    <xsl:apply-templates/>
                </h3>
            </xsl:when>
            <xsl:when test="span[contains(@style,'heading: h4')]">
                <h4>
                   <xsl:apply-templates/>
                </h4>
            </xsl:when>
            <xsl:when test="span[contains(@style,'heading: h5')]">
                <h5>
                    <xsl:apply-templates/>
                </h5>
            </xsl:when>
            <xsl:when test="span[contains(@style,'heading: h6')]">
                <h6>
                    <xsl:apply-templates/>
                </h6>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="span[contains(@style,'heading:')]">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="img">
        <img>
            <xsl:copy-of select="@*[not(name()='use')]"/>
            <xsl:attribute name="style">
                <xsl:value-of select="concat('margin:0 20px 10px 0px;float:left;',@style)"/>
            </xsl:attribute>
            <xsl:attribute name="alt">
                <xsl:value-of select="@use"/>
            </xsl:attribute>
            <xsl:attribute name="src">
                <xsl:value-of select="@use"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </img>
    </xsl:template>
    
    <xsl:template match="html|body|span[count(@*)=0]">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="head">
    </xsl:template>
    
    <xsl:template match="br">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*" >
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="/">
        <html>
            <head>
                <title>FROM EPUB</title>
            </head>
            <body>
                <div style="margin:20px 20px 20px 20px; font-family: arial, helvetica, sans-serif; font-size: 12pt;line-height: 1.5em;">
                    <xsl:apply-templates/>
                </div>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
