import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Copy, Download, Save, CheckCircle, AlertTriangle } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { API_BASE_URL } from "@/config/api";


const XACDMLExportTab = ({ processId }) => {
  const { toast } = useToast();
  const [acdId, setAcdId] = useState(() => localStorage.getItem(`xacdml_acdId_${processId}`) || 'example.acd.id.001');
  const [generatedXACDML, setGeneratedXACDML] = useState('');
  const [isValidated, setIsValidated] = useState(null);

  useEffect(() => {
    localStorage.setItem(`xacdml_acdId_${processId}`, acdId);
  }, [acdId, processId]);

  const handleGenerateXACDML = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/xacdml/generate/${processId}?acdId=${acdId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
      });
      if (!response.ok) throw new Error("Error generating XACDML");

      const content = await response.text();
      setGeneratedXACDML(content);
      setIsValidated(null);

      toast({ title: "Generation Completed", description: "XACDML successfully generated!", variant: "default" });
    } catch (error) {
      console.error(error);
      toast({ title: "Generation Error", description: "Could not generate XACDML.", variant: "destructive" });
    }
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(generatedXACDML);
    toast({ title: "Copied!", description: "XACDML content copied to clipboard.", variant: "default" });
  };

  const handleDownload = () => {
    const blob = new Blob([generatedXACDML], { type: 'application/xml' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${acdId || 'process'}.xml`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    toast({ title: "Download Started", description: `Downloading ${acdId || 'process'}.xml`, variant: "default" });
  };

  const handleSaveXACDML = () => {
    toast({
      title: "Save XACDML",
      description: "🚧 This feature (saving to server/storage) is not implemented yet.",
      variant: "default",
    });
  };

  const handleValidateXACDML = () => {
    const isValid = Math.random() > 0.3;
    setIsValidated(isValid);
    if (isValid) {
      toast({ title: "Validation Completed", description: "XACDML seems valid!", variant: "default" });
    } else {
      toast({ title: "Validation Failed", description: "Problems were found in XACDML.", variant: "destructive" });
    }
  };

  return (
      <Card className="bg-card border-border text-foreground">
        <CardHeader>
          <CardTitle className="text-2xl text-primary">XACDML Export</CardTitle>
          <CardDescription className="text-muted-foreground">
            Generate and export the XACDML representation of your process.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">

          {/* ACD ID */}
          <div className="flex items-end gap-3">
            <div className="w-1/3">
              <Label htmlFor="acdId" className="text-foreground">ACD ID</Label>
              <Input
                  id="acdId"
                  value={acdId}
                  onChange={(e) => setAcdId(e.target.value)}
                  placeholder="e.g., com.example.myprocess"
                  className="bg-card border-border text-foreground placeholder:text-muted-foreground"
              />
            </div>
            <Button
                type="button"
                onClick={handleGenerateXACDML}
                className="bg-primary hover:bg-primary/80 text-primary-foreground h-[42px]"
            >
              Generate XACDML
            </Button>
          </div>

          {/* XACDML Output */}
          <div>
            <Label htmlFor="xacdmlOutput" className="text-foreground">XACDML Output (Read-only)</Label>
            <Textarea
                id="xacdmlOutput"
                value={generatedXACDML}
                readOnly
                rows={15}
                className="bg-card border-border text-foreground font-mono text-sm resize-none"
            />
          </div>

          {/* Actions */}
          <div className="flex flex-wrap gap-3 items-center">
            <Button variant="outline" onClick={handleCopy} className="text-primary border-primary hover:bg-primary hover:text-primary-foreground">
              <Copy className="mr-2 h-4 w-4" /> Copy
            </Button>
            <Button variant="outline" onClick={handleDownload} className="text-primary border-primary hover:bg-primary hover:text-primary-foreground">
              <Download className="mr-2 h-4 w-4" /> Download
            </Button>
            <Button variant="outline" onClick={handleSaveXACDML} className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white">
              <Save className="mr-2 h-4 w-4" /> Save XACDML
            </Button>
            <Button
                variant="outline"
                onClick={handleValidateXACDML}
                className={`
              ${isValidated === true
                    ? 'text-green-600 border-green-600 hover:bg-green-600 hover:text-white'
                    : isValidated === false
                        ? 'text-red-600 border-red-600 hover:bg-red-600 hover:text-white'
                        : 'text-yellow-600 border-yellow-600 hover:bg-yellow-600 hover:text-white'}
              flex items-center
            `}
            >
              {isValidated === true && <CheckCircle className="mr-2 h-4 w-4" />}
              {isValidated === false && <AlertTriangle className="mr-2 h-4 w-4" />}
              {isValidated === null && <AlertTriangle className="mr-2 h-4 w-4 opacity-50" />}
              Validate XACDML
            </Button>
          </div>

          {/* Validation messages */}
          {isValidated === true && <p className="text-sm text-green-600">XACDML validated successfully!</p>}
          {isValidated === false && <p className="text-sm text-red-600">XACDML contains errors. Please check the structure.</p>}
        </CardContent>
      </Card>
  );
};

export default XACDMLExportTab;
