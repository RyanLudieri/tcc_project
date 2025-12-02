import React from "react";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { PlusCircle, Save, X } from "lucide-react";
import { Label } from "@/components/ui/label.jsx";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select.jsx";

const ObserversSection = ({
                              observers,
                              workProducts,
                              isAddingObserver,
                              selectedWorkProduct,
                              selectedType,
                              showAddObserverForm,
                              setSelectedWorkProduct,
                              setSelectedType,
                              observerTypes,
                              handleAddObserver,
                              cancelAddObserver,
                              toggleObserverEdit,
                              handleObserverTypeChange,
                              saveUpdateObserver,
                              handleRemoveObserver,
                          }) => {
    return (
        <Card className="bg-card border-border text-foreground flex-none w-1/2 h-[calc(100vh-200px)] flex flex-col">
            <CardHeader>
                <CardTitle className="text-2xl text-primary">Observers for Work Products queue's</CardTitle>
                <CardDescription className="text-muted-foreground">
                    Manage global observers for queues in this process.
                </CardDescription>
            </CardHeader>
            <CardContent>

                <Button
                    onClick={showAddObserverForm}
                    className="bg-primary hover:bg-primary/90 text-primary-foreground mb-4"
                >
                    <PlusCircle className="mr-2 h-5 w-5" /> Add Observer
                </Button>

                {isAddingObserver && (
                    <div className="mb-4 p-4 border border-border rounded-lg bg-muted">
                        <h3 className="text-lg font-semibold text-primary mb-3">Add New Observer</h3>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div>
                                <Label className="text-foreground mb-2">Select Queue Name</Label>
                                <Select value={selectedWorkProduct} onValueChange={setSelectedWorkProduct}>
                                    <SelectTrigger className="bg-card border-border text-foreground">
                                        <SelectValue placeholder="Choose a queue" />
                                    </SelectTrigger>
                                    <SelectContent className="bg-card border-border text-foreground">
                                        {workProducts.map((wp) => (
                                            <SelectItem key={wp.queueName} value={wp.queueName} className="hover:bg-muted">
                                                {wp.queueName}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <div>
                                <Label className="text-foreground mb-2">Observer Type</Label>
                                <Select value={selectedType} onValueChange={setSelectedType}>
                                    <SelectTrigger className="bg-card border-border text-foreground">
                                        <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent className="bg-card border-border text-foreground">
                                        {observerTypes.map((t) => (
                                            <SelectItem key={t} value={t} className="hover:bg-muted">{t}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex items-end gap-2">
                                <Button
                                    onClick={handleAddObserver}
                                    className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2"
                                >
                                    <Save className="h-4 w-4 shrink-0" />
                                    <span>Add</span>
                                </Button>

                                <Button
                                    onClick={cancelAddObserver}
                                    variant="outline"
                                    className="flex items-center gap-2 text-foreground border-border hover:bg-muted px-4 py-2"
                                >
                                    <X className="h-4 w-4 shrink-0" />
                                    <span>Cancel</span>
                                </Button>
                            </div>
                        </div>
                    </div>
                )}

                {/* LISTA DE OBSERVERS */}
                <div className="space-y-3">
                    {observers.length === 0 ? (
                        <p className="text-sm text-muted-foreground">No observers added.</p>
                    ) : (
                        observers.map((obs) => (
                            <div key={obs.id} className="p-4 border border-border rounded-lg bg-card shadow-sm">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-sm font-medium text-primary">{obs.queueName}</p>

                                        {obs.isEditing ? (
                                            <Select
                                                value={obs.type}
                                                onValueChange={(v) => handleObserverTypeChange(obs.id, v)}
                                            >
                                                <SelectTrigger className="bg-card border-border text-foreground mt-2">
                                                    <SelectValue />
                                                </SelectTrigger>
                                                <SelectContent className="bg-card border-border text-foreground">
                                                    {observerTypes.map((t) => (
                                                        <SelectItem key={t} value={t}>
                                                            {t}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        ) : (
                                            <p className="text-sm text-muted-foreground">{obs.type}</p>
                                        )}
                                    </div>

                                    <div className="flex gap-2">
                                        {obs.isEditing ? (
                                            <Button
                                                size="sm"
                                                className="bg-green-600 hover:bg-green-700 text-white"
                                                onClick={() => saveUpdateObserver(obs.id)}
                                            >
                                                Save
                                            </Button>
                                        ) : (
                                            <Button
                                                size="sm"
                                                variant="outline"
                                                onClick={() => toggleObserverEdit(obs.id)}
                                            >
                                                Edit
                                            </Button>
                                        )}

                                        <Button
                                            size="sm"
                                            variant="destructive"
                                            onClick={() => handleRemoveObserver(obs.id)}
                                        >
                                            Delete
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>

            </CardContent>
        </Card>
    );
};

export default ObserversSection;
